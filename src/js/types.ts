import { Path } from "react-hook-form";

export type SidebarProps = {
  user?: User;
  navigation: NavigationItem[];
  secondaryNavigation: NavigationItem[];
};

export type NavigationItem = {
  name: string;
  path: string;
  id?: string;
  current?: boolean;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  icon?: React.ComponentType<any>;
};

export type Fields = {
  [index: string]: string;
};

export type User = {
  name: string;
  email?: string;
  projects: Option[];
  id?: string;
  role?: string;
  handle?: string;
  coverImageUrl?: string;
  imageUrl?: string;
  about?: string;
  fields?: Fields;
  holidays?: CalendarFormData[];
};

export type Directory = {
  [index: string]: User[];
};

export type TonDeleteEvent = (id: string) => void;
export type TonUpdateEvent = (props: unknown) => void;

export type PeopleProps = {
  profile?: User;
  isProfileLoading: boolean;
  directory?: Directory;
  isDirectoryLoading: boolean;
  isCurrentUser: boolean;
  onUpdateEvent: TonUpdateEvent;
  onDeleteEvent: TonDeleteEvent;
};

export type EventType = "Holiday" | "Timesheet";

export type CalendarFormData = {
  id?: string;
  title: string;
  project?: string;
  type: EventType;
  start: string;
  isStartHalfDay?: boolean;
  end: string;
  isEndHalfDay?: boolean;
  allDay?: boolean;
};

export type Option = {
  label?: string;
  value: string;
};

export type FormInput = {
  inputName: Path<CalendarFormData>;
  label?: string;
  type: string;
  options?: Option[];
  placeholder?: string;
  wrapperClass?: string;
  inputClass?: string;
  required?: boolean;
};
