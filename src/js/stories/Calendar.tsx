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
  projectOptions: Option[];
  isCurrentUser: boolean;
  onUpdateEvent: TonUpdateEvent;
  onDeleteEvent: TonDeleteEvent;
};

export type CalendarModalProps = CalendarFormData | null | undefined;

export function EventCalendar({
  events,
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
        start: startStr,
        end: endStr,
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

  return (
    <div className="demo-app">
      <CreateEventForm
        dateRange={modalProps}
        projectOptions={projectOptions}
        setDateRange={setModalProps}
        onSubmit={onUpdateEvent}
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
          eventClick={handleEventClick}
          eventsSet={setCurrentEvents}
          eventDrop={handleEventChange}
          eventResize={handleEventChange}
        />
      </div>
    </div>
  );
}
