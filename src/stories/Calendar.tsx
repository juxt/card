import "react-big-calendar/lib/css/react-big-calendar.css";
import { Calendar, Views, dateFnsLocalizer } from "react-big-calendar";
import format from "date-fns/format";
import parse from "date-fns/parse";
import startOfWeek from "date-fns/startOfWeek";
import getDay from "date-fns/getDay";
import enGB from "date-fns/locale/en-GB";

const locales = {
  "en-GB": enGB,
};

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek,
  getDay,
  locales,
});

export type CalendarProps = {
  events: Array<{
    allDay?: boolean | undefined;
    title?: string | undefined;
    start?: Date | undefined;
    end?: Date | undefined;
    resource?: any;
    desc?: string;
  }>;
};

let BasicCalendar = ({ events }: CalendarProps) => {
  return (
    <div className="h-full p-4">
      <Calendar
        events={events || []}
        step={15}
        timeslots={8}
        localizer={localizer}
        defaultView={Views.WEEK}
        defaultDate={new Date()}
      />
    </div>
  );
};

export default BasicCalendar;
