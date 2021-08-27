import { DateSelectArg } from "@fullcalendar/react";
import { Dialog } from "@headlessui/react";
import { FC, useRef } from "react";
import { useCalendarForm } from "../hooks/useCalendarForm";
import { CalendarFormData } from "../types";
import Modal from "./Modal";

export type SubmitEventFn = (props: CalendarFormData) => void;

export type Props = {
  dateRange: DateSelectArg | null;
  setDateRange: (dateRange: DateSelectArg | null) => void;
  onSubmit: SubmitEventFn;
};

export const CreateEventForm: FC<Props> = ({
  dateRange,
  setDateRange,
  ...props
}) => {
  const confirmButtonRef = useRef(null);
  const { register, onSubmit, errors, reset } = useCalendarForm(
    props.onSubmit,
    dateRange
  );
  //I think I need the hook in the modal to make things work.. a little messy but whatever

  const formatDate = (dateStr: string | undefined) => {
    if (!dateStr) return "";
    return dateStr.length === 10 ? `${dateStr}T00:00:00` : dateStr.slice(0, 19);
  };

  return (
    <Modal
      setOpen={setDateRange}
      initialRef={confirmButtonRef}
      open={!!dateRange}
      onClose={reset}
    >
      <form
        onSubmit={(e) => {
          onSubmit(e);
          setDateRange(null);
        }}
      >
        <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
          <div className="sm:flex sm:items-start">
            <div className="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left">
              <Dialog.Title
                as="h3"
                className="text-lg leading-6 font-medium text-gray-900"
              >
                Create new event
              </Dialog.Title>
              <>
                <input type="text" {...register("title")} />
                <input
                  type="datetime-local"
                  defaultValue={
                    (dateRange && formatDate(dateRange.startStr)) || ""
                  }
                  {...register("start")}
                />
                <input
                  type="datetime-local"
                  defaultValue={formatDate(dateRange?.endStr)}
                  {...register("end")}
                />
              </>
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
    </Modal>
  );
};
