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
  dateRange: DateSelectArg | null,
  setDateRange: (dateRange: DateSelectArg | null) => void
) {
  const validationSchema = useMemo(
    () =>
      yup.object().shape({
        description: yup.string().required("Please provide a description"),
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
    formState: { errors },
  } = useForm<CalendarFormData>({
    resolver: yupResolver(validationSchema),
  });

  const onSubmit: SubmitHandler<CalendarFormData> = (formValues) => {
    if (dateRange?.start && dateRange?.end) {
      const calendarApi = dateRange.view.calendar;
      calendarApi.unselect(); // clear date selection
      calendarApi.addEvent({
        id: createEventId(),
        title: formValues.description,
        start: dateRange.startStr,
        end: dateRange.endStr,
        allDay: dateRange.allDay,
      });
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
    reset,
    errors,
    onSubmit: handleSubmit(onSubmit, onError),
  };
}
