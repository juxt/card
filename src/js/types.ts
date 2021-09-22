export type SidebarProps = {
  user: User;
  navigation: NavigationItem[];
  secondaryNavigation: NavigationItem[];
};

export type NavigationItem = {
  name: string;
  href?: string;
  id?: string;
  path?: string;
  label?: string;
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
  user: User;
  profile: User;
  directory: Directory;
  isCurrentUser: boolean;
  onUpdateEvent: TonUpdateEvent;
  onDeleteEvent: TonDeleteEvent;
};

export type CalendarFormData = {
  id?: string;
  title: string;
  project?: string;
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
