import React from "react";

export type NavigationItem = {
  name: string;
  href: string;
  current: boolean;
  icon: React.ComponentType<any>;
};
