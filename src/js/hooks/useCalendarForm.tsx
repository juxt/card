import { useMemo } from "react";
import { SubmitErrorHandler, SubmitHandler, useForm } from "react-hook-form";
import { CalendarFormData } from "../types";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { SubmitEventFn } from "../stories/CreateEventForm";
import { CalendarModalProps } from "../stories/Calendar";

export function useCalendarForm(
  submitFn: SubmitEventFn,
  dateRange: CalendarModalProps,
  setDateRange: (dateRange: CalendarModalProps) => void
) {
  const validationSchema = useMemo(
    () =>
      yup.object().shape({
        title: yup.string().required("Please provide a description"),
        start: yup.date().required("Start date is requred"),
        end: yup
          .date()
          .required("End date is requred")
          .min(yup.ref("start"), "End date must be after start date"),
      }),
    []
  );

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm<CalendarFormData>({
    //disable typescript for this yup stuff until I can figure it out
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    resolver: yupResolver(validationSchema),
  });

  const onSubmit: SubmitHandler<CalendarFormData> = (formValues) => {
    if (dateRange?.start && dateRange?.end) {
      // fn passed from cljs, calling it updates events in reframe which rerenders calendar
      submitFn(formValues);
      reset();
      //closes modal
      setDateRange(null);
    }
  };

  const onError: SubmitErrorHandler<CalendarFormData> = (errors) => {
    console.log("Calendar form errors", errors);
  };

  return {
    register,
    setValue,
    errors,
    onSubmit: handleSubmit(onSubmit, onError),
  };
}
