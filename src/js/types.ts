import { EventInput, ViewApi } from "@fullcalendar/react";
import React from "react";

export type SidebarProps = {
  user: User;
  navigation: NavigationItem[];
  secondaryNavigation: NavigationItem[];
};

export type NavigationItem = {
  name: string;
  href?: string;
  id?: string;
  target?: string;
  label?: string;
  current?: boolean;
  icon: React.ComponentType<any>;
};

export type Fields = {
  [index: string]: string;
};

export type User = {
  name: string;
  id?: string;
  role?: string;
  handle?: string;
  coverImageUrl?: string;
  imageUrl?: string;
  about?: string;
  fields?: Fields;
  holidays?: EventInput[];
};

export type Directory = {
  [index: string]: User[];
};

export type TonDeleteEvent = (id: string) => void;
export type TonUpdateEvent = (props: any) => void;

export type PeopleProps = {
  user: User;
  profile: User;
  directory: Directory;
  onUpdateEvent: TonUpdateEvent;
  onDeleteEvent: TonDeleteEvent;
};

export type CalendarFormData = {
  id?: string;
  description: string;
  start: string;
  end: string;
  allDay?: boolean;
};
