import React from "react";
import FullCalendar, {
  DateSelectArg,
  EventClickArg,
  EventContentArg,
  formatDate,
  EventInput,
  EventApi,
} from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import useMobileDetect, { createEventId } from "../utils";
import Modal from "./Modal";
import { ExclamationIcon } from "@heroicons/react/solid";
import { Dialog } from "@headlessui/react";
import { CreateEventForm } from "./CreateEventForm";
import { CalendarFormData } from "../types";

export type CalendarProps = {
  initialEvents: EventInput[];
  onCreateEvent: (props: CalendarFormData) => void;
};

export function BasicCalendar({ initialEvents, onCreateEvent }: CalendarProps) {
  const [currentEvents, setCurrentEvents] = React.useState<EventApi[]>([]);
  const [weekendsVisible, setWeekendsVisible] = React.useState(true);
  const isMobile = useMobileDetect();
  const [modalProps, setModalProps] = React.useState<DateSelectArg | null>(
    null
  );

  const handleWeekendsToggle = () => {
    setWeekendsVisible(!weekendsVisible);
  };

  const handleEventClick = (clickInfo: EventClickArg) => {
    if (
      confirm(
        `Are you sure you want to delete the event '${clickInfo.event.title}'`
      )
    ) {
      clickInfo.event.remove();
    }
  };

  const renderEventContent = (eventContent: EventContentArg) => (
    <>
      <b>{eventContent.timeText}</b>
      <i>{eventContent.event.title}</i>
    </>
  );

  const renderSidebarEvent = (event: EventApi, index: number) => (
    <li key={event.id}>
      <b>
        {formatDate(event.start!, {
          year: "numeric",
          month: "short",
          day: "numeric",
        })}
      </b>
      <i>{event.title}</i>
    </li>
  );
  return (
    <div className="demo-app">
      <CreateEventForm
        dateRange={modalProps}
        setDateRange={setModalProps}
        onSubmit={onCreateEvent}
      />
      <div className="demo-app-sidebar">
        <div className="demo-app-sidebar-section">
          <h2>Instructions</h2>
          <ul>
            <li>Select dates and you will be prompted to create a new event</li>
            <li>Drag, drop, and resize events</li>
            <li>Click an event to delete it</li>
          </ul>
        </div>
        <div className="demo-app-sidebar-section">
          <label>
            <input
              type="checkbox"
              checked={weekendsVisible}
              onChange={handleWeekendsToggle}
            ></input>
            toggle weekends
          </label>
        </div>
        <div className="demo-app-sidebar-section">
          <h2>All Events ({currentEvents.length})</h2>
          <ul>{currentEvents.map(renderSidebarEvent)}</ul>
        </div>
      </div>
      <div className="demo-app-main">
        <FullCalendar
          plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
          headerToolbar={{
            left: "prev,next today",
            center: "title",
            right: isMobile ? "" : "dayGridMonth,timeGridWeek,timeGridDay",
          }}
          initialView="dayGridMonth"
          contentHeight="auto"
          editable={true}
          selectable={true}
          selectMirror={true}
          dayMaxEvents={true}
          weekends={weekendsVisible}
          initialEvents={initialEvents}
          select={setModalProps}
          eventContent={renderEventContent} // custom render function
          eventClick={handleEventClick}
          eventsSet={setCurrentEvents} // called after events are initialized/added/changed/removed
          /* you can update a remote database when these fire:
            eventAdd={function(){}}
            eventChange={function(){}}
            eventRemove={function(){}}
            */
        />
      </div>
    </div>
  );
}
