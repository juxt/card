import { useCallback, useMemo } from "react";
import { SubmitErrorHandler, SubmitHandler, useForm } from "react-hook-form";
import { CalendarFormData } from "../types";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { SubmitEventFn } from "../stories/CreateEventForm";
import { DateSelectArg } from "@fullcalendar/react";
import { createEventId } from "../utils";

export function useCalendarForm(
  submitFn: SubmitEventFn,
  dateRange: DateSelectArg | null
) {
  const validationSchema = useMemo(
    () =>
      yup.object().shape({
        title: yup.string().required("Title is required"),
        start: yup.string().required("Start date is requred"),
        end: yup.string().required("End date is requred"),
      }),
    []
  );

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CalendarFormData>({
    resolver: yupResolver(validationSchema),
  });

  const onSubmit: SubmitHandler<CalendarFormData> = (formValues) => {
    console.log("form", formValues);
    if (dateRange) {
      const calendarApi = dateRange.view.calendar;
      calendarApi.unselect(); // clear date selection
      if (formValues?.title) {
        calendarApi.addEvent({
          id: createEventId(),
          title: formValues.title,
          start: dateRange.startStr,
          end: dateRange.endStr,
          allDay: dateRange.allDay,
        });
      }
      submitFn(formValues);
      reset();
    }
  };

  const onError: SubmitErrorHandler<CalendarFormData> = (errors) => {
    console.log("errors", errors);
  };

  return {
    register,
    reset,
    errors,
    onSubmit: handleSubmit(onSubmit, onError),
  };
}
