import React from "react";
import FullCalendar, {
  DateSelectArg,
  EventClickArg,
  formatDate,
  EventApi,
} from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import useMobileDetect from "../utils";
import { TrashIcon, PencilIcon } from "@heroicons/react/solid";
import { CreateEventForm } from "./CreateEventForm";
import {
  CalendarFormData,
  TonDeleteEvent,
  TonUpdateEvent,
  Option,
  FormInput,
  EventType,
} from "../types";
import {
  Menu,
  Item,
  Separator,
  useContextMenu,
  ItemParams,
} from "react-contexify";
import "react-contexify/dist/ReactContexify.css";

const MENU_ID = "event-menu-id";

export type CalendarProps = {
  events: CalendarFormData[];
  eventType?: EventType;
  projectOptions?: Option[];
  isCurrentUser: boolean;
  isLoading?: boolean;
  onUpdateEvent: TonUpdateEvent;
  onDeleteEvent: TonDeleteEvent;
};

export type CalendarModalProps = CalendarFormData | null | undefined;

export function EventCalendar({
  events,
  eventType,
  isLoading,
  isCurrentUser,
  projectOptions,
  onUpdateEvent,
  onDeleteEvent,
}: CalendarProps) {
  const [currentEvents, setCurrentEvents] = React.useState<EventApi[]>([]);
  const [weekendsVisible, setWeekendsVisible] = React.useState(true);
  const [menuVisible, setMenuVisible] = React.useState(false);
  const isMobile = useMobileDetect();
  const [modalProps, setModalProps] = React.useState<CalendarModalProps>(null);
  const { show } = useContextMenu({ id: MENU_ID });
  const [selectedEvent, setSelectedEvent] =
    React.useState<CalendarModalProps>(null);
  const handleWeekendsToggle = () => {
    setWeekendsVisible(!weekendsVisible);
  };

  const filteredEvents = React.useMemo(() => {
    if (!eventType) return events;
    return events.filter((event) => event.type === eventType);
  }, [events, eventType]);

  function convertEvent(event: EventApi) {
    const id = event.id;
    const calEvent = events.find((e) => e.id === id);
    return calEvent;
  }

  const handleEventClick = (clickProps: EventClickArg) => {
    show(clickProps.jsEvent, { props: clickProps });
    console.log(clickProps);
    setSelectedEvent(convertEvent(clickProps.event));
  };

  const handleEventChange = (dropProps: EventClickArg) => {
    const { event } = dropProps;
    onUpdateEvent(event.toPlainObject());
  };

  const handleSelect = ({ startStr, endStr, allDay }: DateSelectArg) => {
    !menuVisible &&
      setModalProps({
        start: startStr + "T00:00",
        end: endStr + "T00:00",
        type: eventType || (allDay ? "Holiday" : "Timesheet"),
        id: "",
        allDay,
        title: "",
      });
  };

  function handleItemClick({ event, props }: ItemParams<EventClickArg>) {
    const calEvent = props!.event;
    const { title, startStr, endStr } = calEvent;

    switch (event.currentTarget.id) {
      case "delete":
        onDeleteEvent(calEvent.id);
        break;
      case "edit":
        setModalProps({
          ...selectedEvent,
          title,
          type: calEvent.extendedProps.type,
          start: startStr,
          end: endStr,
        });
        break;
    }
  }

  const renderSidebarEvent = (event: EventApi) => (
    <li key={event.id}>
      {event?.start && (
        <b>
          {formatDate(event.start, {
            year: "numeric",
            month: "short",
            day: "numeric",
          })}
        </b>
      )}
      <i>{event.title}</i>
    </li>
  );
  const checkboxClass =
    "mt-2.5 rounded-md focus:ring-indigo-500 focus:border-indigo-500 min-w-0";
  const noDivider = "sm:grid sm:grid-cols-3 sm:gap-4 sm:items-start sm:pt-1";

  const holidayInputs: FormInput[] = [
    {
      inputName: "title",
      label: "Description",
      type: "text",
      placeholder: "Going anywhere nice?",
    },
    {
      inputName: "start",
      label: "Start Date",
      type: "date",
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
      type: "date",
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
  const timesheetInputs: FormInput[] = [
    {
      inputName: "project",
      label: "Project",
      type: "dropdown",
      options: projectOptions,
      placeholder: "Project",
    },
    {
      inputName: "title",
      label: "Short description of the work",
      type: "text",
      placeholder: "",
    },
    {
      inputName: "start",
      label: "Start Date",
      type: "datetime-local",
      required: true,
    },
    {
      inputName: "end",
      label: "End Date",
      type: "datetime-local",
      required: true,
    },
  ];
  const isHoliday = modalProps?.type === "Holiday";
  // for now assume timesheets if not holiday
  const inputs = isHoliday ? holidayInputs : timesheetInputs;
  const modalTitle = isHoliday ? "Add Holiday" : "Add Timesheet";
  return (
    <div className="demo-app">
      <CreateEventForm
        dateRange={modalProps}
        setDateRange={setModalProps}
        onSubmit={onUpdateEvent}
        inputs={inputs}
        title={modalTitle}
      />
      <div className="demo-app-sidebar">
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
          {isLoading && <div>Loading...</div>}
          <h2>All Events ({currentEvents.length})</h2>
          <ul>{currentEvents.map(renderSidebarEvent)}</ul>
        </div>
      </div>
      <div className="demo-app-main">
        {isCurrentUser && (
          <Menu
            id={MENU_ID}
            onShown={() => setMenuVisible(true)}
            onHidden={() => setMenuVisible(false)}
          >
            <Item disabled>
              <h1 className="font-bold">{selectedEvent?.title}</h1>
            </Item>
            {selectedEvent?.start && (
              <>
                <Item disabled>
                  <p>
                    From: {selectedEvent?.start.toString()}
                    {selectedEvent?.isStartHalfDay && " (Half Day)"}
                  </p>
                </Item>
                <Item disabled>
                  <p>
                    To: {selectedEvent?.end.toString()}
                    {selectedEvent?.isEndHalfDay && " (Half Day)"}
                  </p>
                </Item>
              </>
            )}
            <Separator />
            <Item id="delete" onClick={handleItemClick}>
              <TrashIcon className="max-h-8 pr-2 text-red-500" />
              <span>Delete Event</span>
            </Item>
            <Separator />
            <Item id="edit" onClick={handleItemClick}>
              <PencilIcon className="max-h-8 pr-2 text-gray-500" />
              Edit Event
            </Item>
          </Menu>
        )}

        <FullCalendar
          plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
          headerToolbar={{
            left: "prev,next today",
            center: "title",
            right:
              isMobile || eventType === "Holiday"
                ? ""
                : "dayGridMonth,timeGridWeek,timeGridDay",
          }}
          eventClassNames={["chromatic-ignore"]}
          initialView="dayGridMonth"
          contentHeight="auto"
          editable={isCurrentUser}
          eventResizableFromStart={isCurrentUser}
          selectable={isCurrentUser}
          selectMirror={isCurrentUser}
          dayMaxEvents={true}
          weekends={weekendsVisible}
          events={filteredEvents}
          select={handleSelect}
          eventClick={handleEventClick}
          eventsSet={setCurrentEvents}
          eventDrop={handleEventChange}
          eventResize={handleEventChange}
        />
      </div>
    </div>
  );
}
