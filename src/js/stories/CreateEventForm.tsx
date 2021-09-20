import { Dialog } from "@headlessui/react";
import { FC, useEffect, useRef } from "react";
import { Path } from "react-hook-form";
import { useCalendarForm } from "../hooks/useCalendarForm";
import { CalendarFormData } from "../types";
import { CalendarModalProps } from "./Calendar";
import Modal from "./Modal";

export type SubmitEventFn = (props: CalendarFormData) => void;

export type Props = {
  dateRange: CalendarModalProps;
  setDateRange: (dateRange: CalendarModalProps) => void;
  onSubmit: SubmitEventFn;
};

type FormInputs = {
  inputName: Path<CalendarFormData>;
  label?: string;
  type: string;
  placeholder?: string;
  wrapperClass?: string;
  inputClass?: string;
  required?: boolean;
}[];

export const CreateEventForm: FC<Props> = (props) => {
  const confirmButtonRef = useRef(null);

  const { dateRange, setDateRange } = props;

  const formatDate = (dateStr: string | undefined) => {
    if (!dateStr) return "";
    if (dateRange?.allDay && dateStr.length === 10) {
      return dateStr;
    }
    return dateStr.length === 10 ? `${dateStr}T00:00:00` : dateStr.slice(0, 19);
  };

  const Form: FC<Props> = ({ setDateRange }) => {
    const { register, onSubmit, errors, reset } = useCalendarForm(
      props.onSubmit,
      dateRange,
      setDateRange
    );

    useEffect(() => {
      reset({
        ...dateRange,
        start: formatDate(dateRange?.start),
        end: formatDate(dateRange?.end),
      });
    }, [reset, dateRange]);

    const checkboxClass =
      "mt-2.5 rounded-md focus:ring-indigo-500 focus:border-indigo-500 min-w-0";
    const noDivider = "sm:grid sm:grid-cols-3 sm:gap-4 sm:items-start sm:pt-1";

    const inputs: FormInputs = [
      {
        inputName: "title",
        label: "Description",
        type: "text",
        placeholder: "Going anywhere nice?",
      },
      {
        inputName: "start",
        label: "Start Date",
        type: dateRange?.allDay ? "date" : "datetime-local",
        required: true,
      },
      {
        inputName: "isStartHalfDay",
        label: "Half Day?",
        type: "checkbox",
        inputClass: checkboxClass,
        wrapperClass: noDivider,
      },
      {
        inputName: "end",
        label: "End Date",
        type: dateRange?.allDay ? "date" : "datetime-local",
        required: true,
      },
      {
        inputName: "isEndHalfDay",
        label: "Half Day?",
        type: "checkbox",
        inputClass: checkboxClass,
        wrapperClass: noDivider,
      },
      {
        inputName: "allDay",
        type: "hidden",
        required: false,
        wrapperClass: noDivider,
      },
    ];
    return (
      <form className="space-y-8 divide-y divide-gray-200" onSubmit={onSubmit}>
        <div className="space-y-8 divide-y divide-gray-200 sm:space-y-5">
          <div>
            <Dialog.Title
              as="h3"
              className="text-lg leading-6 font-medium text-gray-900"
            >
              Create new event
            </Dialog.Title>

            <div className="mt-6 sm:mt-5 space-y-6 sm:space-y-5">
              <input type="hidden" {...register("id")} />

              {inputs.map(
                ({ inputName, wrapperClass, inputClass, ...inputProps }) => (
                  <div
                    key={inputName}
                    className={
                      wrapperClass ??
                      "sm:grid sm:grid-cols-3 sm:gap-4 sm:items-start sm:border-t sm:border-gray-200 sm:pt-5"
                    }
                  >
                    {inputProps?.label && (
                      <label
                        htmlFor={inputName}
                        className={`block text-sm font-medium sm:mt-px sm:pt-2 ${
                          errors[inputName] ? "text-red-600" : "text-gray-700"
                        }`}
                      >
                        {inputProps.label}
                      </label>
                    )}
                    <div className="mt-1 sm:mt-0 sm:col-span-2">
                      <div className="max-w-lg flex">
                        <input
                          {...inputProps}
                          {...register(inputName)}
                          className={
                            inputClass ||
                            "flex-1 block w-full rounded-md focus:ring-indigo-500 focus:border-indigo-500 min-w-0 sm:text-sm border-gray-300"
                          }
                        />
                      </div>
                    </div>
                    {errors[inputName] && (
                      <>
                        <p className="mt-2 text-sm text-red-600">
                          {errors[inputName]?.message}
                        </p>
                      </>
                    )}
                  </div>
                )
              )}
            </div>
          </div>
        </div>

        <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
          <button
            type="submit"
            className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:ml-3 sm:w-auto sm:text-sm"
            name="submit"
            ref={confirmButtonRef}
          >
            Save
          </button>
          <button
            type="button"
            className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
            onClick={() => {
              setDateRange(null);
            }}
          >
            Cancel
          </button>
        </div>
      </form>
    );
  };

  return (
    <Modal
      setOpen={setDateRange}
      initialRef={confirmButtonRef}
      open={dateRange}
    >
      <div className="p-4">
        <Form {...props} />
      </div>
    </Modal>
  );
};
