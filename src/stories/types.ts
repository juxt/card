import React from "react";

export type NavigationItem = {
  name: string;
  href: string;
  current: boolean;
  icon: React.ComponentType<any>;
};

export type User = {
  name: string;
  id: string;
  coverImageUrl?: string;
  imageUrl?: string;
  fields: Object;
};

export type PeopleProps = {
  user: User;
  profile: User;
  directory: User[];
};
