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
import { CalendarFormData, TonDeleteEvent, TonUpdateEvent } from "../types";
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
  events: EventInput[];
  isCurrentUser: boolean;
  onUpdateEvent: TonUpdateEvent;
  onDeleteEvent: TonDeleteEvent;
};

export type CalendarModalProps = CalendarFormData | null;

export function EventCalendar({
  events,
  isCurrentUser,
  onUpdateEvent,
  onDeleteEvent,
}: CalendarProps) {
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

  const handleEventChange = (dropProps: EventClickArg) => {
    const { event } = dropProps;
    const { id, start, end, allDay, title } = event;
    onUpdateEvent({
      id,
      start: start!,
      end: end!,
      allDay,
      description: title,
    });
  };

  const handleSelect = ({ startStr, endStr, allDay }: DateSelectArg) => {
    !menuVisible &&
      setModalProps({
        start: startStr,
        end: endStr,
        id: "",
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
    const calEvent = props!.event;
    switch (event.currentTarget.id) {
      case "delete":
        onDeleteEvent(calEvent.id);
        break;
      case "edit":
        const { id, title, startStr, endStr, allDay } = calEvent;

        setModalProps({
          id: id,
          description: title,
          start: startStr,
          end: endStr,
          allDay,
        });
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
        onSubmit={onUpdateEvent}
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
        {isCurrentUser && (
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
        )}

        <FullCalendar
          plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
          headerToolbar={{
            left: "prev,next today",
            center: "title",
            right: isMobile ? "" : "dayGridMonth,timeGridWeek,timeGridDay",
          }}
          initialView="dayGridMonth"
          contentHeight="auto"
          editable={isCurrentUser}
          eventResizableFromStart={isCurrentUser}
          selectable={isCurrentUser}
          selectMirror={isCurrentUser}
          dayMaxEvents={true}
          weekends={weekendsVisible}
          events={events}
          select={handleSelect}
          eventContent={renderEventContent} // custom render function
          eventClick={handleEventClick}
          eventsSet={setCurrentEvents}
          eventDrop={handleEventChange}
          eventResize={handleEventChange}
        />
      </div>
    </div>
  );
}
