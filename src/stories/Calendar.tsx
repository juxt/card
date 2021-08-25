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
import useMobileDetect, { createEventId } from "./utils";
import Modal from "./Modal";
import { ExclamationIcon } from "@heroicons/react/solid";
import { Dialog } from "@headlessui/react";
import { CreateEventForm, SubmitEventProps } from "./CreateEventForm";

export type CalendarProps = {
  initialEvents: EventInput[];
  onCreateEvent: (props: SubmitEventProps) => void;
};

export function BasicCalendar({ initialEvents, onCreateEvent }: CalendarProps) {
  const [currentEvents, setCurrentEvents] = React.useState<EventApi[]>([]);
  const [weekendsVisible, setWeekendsVisible] = React.useState(true);
  const [modalProps, setModalProps] = React.useState<DateSelectArg>();
  const handleWeekendsToggle = () => {
    setWeekendsVisible(!weekendsVisible);
  };
  const isMobile = useMobileDetect();

  const saveDateSelect = (selectInfo: DateSelectArg, { title }) => {
    let calendarApi = selectInfo.view.calendar;

    calendarApi.unselect(); // clear date selection
    if (title) {
      calendarApi.addEvent({
        id: createEventId(),
        title,
        start: selectInfo.startStr,
        end: selectInfo.endStr,
        allDay: selectInfo.allDay,
      });
    }
  };
  const handleDateSelect = (selectInfo: DateSelectArg) => {
    let title = "Please enter a new title for your event";
    setModalProps(selectInfo);
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
      <Modal
        onCancel={() => {}}
        onConfirm={() => {}}
        confirmText="Save"
        cancelText="Cancel"
        open={Boolean(modalProps)}
        setOpen={setModalProps}
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
              <CreateEventForm dateRange={modalProps} onSubmit={onCreateEvent} />
           </div>
          </div>
        </div>
      </Modal>
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
          select={handleDateSelect}
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
