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
import { TrashIcon, PencilIcon } from "@heroicons/react/solid";
import { CreateEventForm } from "./CreateEventForm";
import { CalendarFormData } from "../types";
import {
  Menu,
  Item,
  Separator,
  Submenu,
  useContextMenu,
  ItemParams,
  ItemProps,
} from "react-contexify";
import "react-contexify/dist/ReactContexify.css";

const MENU_ID = "event-menu-id";

export type CalendarProps = {
  initialEvents: EventInput[];
  onCreateEvent: (props: CalendarFormData) => void;
};

export type CalendarModalProps = CalendarFormData | null;

export function BasicCalendar({ initialEvents, onCreateEvent }: CalendarProps) {
  const [currentEvents, setCurrentEvents] = React.useState<EventApi[]>([]);
  const [weekendsVisible, setWeekendsVisible] = React.useState(true);
  const [menuVisible, setMenuVisible] = React.useState(false);
  const isMobile = useMobileDetect();
  const [modalProps, setModalProps] = React.useState<CalendarModalProps>(null);
  const { show } = useContextMenu({ id: MENU_ID });

  const handleWeekendsToggle = () => {
    setWeekendsVisible(!weekendsVisible);
  };

  const handleEventClick = (clickProps: EventClickArg) => {
    show(clickProps.jsEvent, { props: clickProps });
  };

  const handleSelect = ({ startStr, endStr, allDay }: DateSelectArg) => {
    !menuVisible &&
      setModalProps({
        start: startStr,
        end: endStr,
        id: createEventId(),
        allDay,
        description: "",
      });
  };

  const renderEventContent = (eventContent: EventContentArg) => (
    <>
      <b>{eventContent.timeText}</b>
      <i>{eventContent.event.title}</i>
    </>
  );

  function handleItemClick({ event, props }: ItemParams<EventClickArg>) {
    switch (event.currentTarget.id) {
      case "delete":
        console.log(props);

        props?.event.remove();
        break;
      case "edit":
        if (props?.event) {
          const {
            id,
            title,
            start,
            end,
            startStr,
            endStr,
            allDay,
          } = props.event;
          console.log(allDay);

          start &&
            end &&
            setModalProps({
              id: id,
              description: title,
              start: startStr,
              end: endStr,
              allDay,
            });
        }
        break;
    }
  }

  const renderSidebarEvent = (event: EventApi) => (
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
        <Menu
          id={MENU_ID}
          onShown={() => setMenuVisible(true)}
          onHidden={() => setMenuVisible(false)}
        >
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
          select={handleSelect}
          eventContent={renderEventContent} // custom render function
          eventClick={handleEventClick}
          eventsSet={setCurrentEvents}
        />
      </div>
    </div>
  );
}
