import { FormInput } from "./FormComponents";
import { DateSelectArg } from "@fullcalendar/react";
import { useForm } from "react-hook-form";

type FormData = {
  firstName: string;
  lastName: string;
};

export type SubmitEventProps = {
  title: string;
  start: Date;
  end: Date;
  allDay?: boolean;
  description?: string;
};

export type CreateEventFormProps = {
  dateRange?: DateSelectArg;
  onSubmit: (props: SubmitEventProps) => void;
};

export const CreateEventForm = ({ dateRange }: CreateEventFormProps) => {
  const {
    register,
    setValue,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>();
  const onSubmit = handleSubmit((data) => console.log(data));

  return (
    <form onSubmit={onSubmit}>
      <FormInput label="f" {...register("firstName")} />
      <FormInput label="b" {...register("lastName")} />
      <button
        type="button"
        onClick={() => {
          setValue("lastName", "luo");
        }}
      >
        SetValue
      </button>
      <div className="mt-2">

        <p className="text-sm text-gray-500">
          Event start date{dateRange?.startStr}
        </p>
        <div className="mt-2">
          <input
            className="form-input block w-full"
            defaultValue={dateRange?.startStr?.slice(0, -6)}
            type="datetime-local"
          />
        </div>
        <p className="text-sm text-gray-500">Event end date</p>
        <div className="mt-2">
          <input
            className="form-input block w-full"
            defaultValue={dateRange?.endStr?.slice(0, -6)}
            type="datetime-local"
          />
        </div>
      </div>
    </form>
  );
};
