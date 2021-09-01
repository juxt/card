/*
  This example requires Tailwind CSS v2.0+

  This example requires some changes to your config:

  ```
  // tailwind.config.js
  module.exports = {
    // ...
    plugins: [
      // ...
      require('@tailwindcss/forms'),
    ]
  }
  ```
*/
import { useState } from "react";
import {
  CalendarIcon,
  CogIcon,
  HomeIcon,
  MapIcon,
  MenuIcon,
  SearchCircleIcon,
  SpeakerphoneIcon,
  UserGroupIcon,
  ViewGridAddIcon,
} from "@heroicons/react/outline";
import { ChevronLeftIcon } from "@heroicons/react/solid";
import { MobileSidebar } from "./MobileSidebar";
import { Sidebar } from "./DesktopSidebar";
import { ProfileHeader } from "./ProfileHeader";
import { Tabs } from "./Tabs";
import { DescriptionList } from "./DescriptionList";
import { TeamMemberList } from "./TeamMemberList";
import { DirectoryList } from "./DirectoryList";
import { PeopleProps } from "../types";
import Breadcrumb from "./Breadcrumb";
import { EventCalendar } from "./Calendar";

const user = {
  name: "Tom Cook",
  imageUrl:
    "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80",
};
const navigation = [
  { name: "Dashboard", href: "#", icon: HomeIcon, current: true },
  { name: "Calendar", href: "#", icon: CalendarIcon, current: false },
  { name: "Teams", href: "#", icon: UserGroupIcon, current: false },
  { name: "Directory", href: "#", icon: SearchCircleIcon, current: false },
  { name: "Announcements", href: "#", icon: SpeakerphoneIcon, current: false },
  { name: "Office Map", href: "#", icon: MapIcon, current: false },
];

const secondaryNavigation = [
  { name: "Apps", href: "#", icon: ViewGridAddIcon },
  { name: "Settings", href: "#", icon: CogIcon },
];

const tabs = [
  { name: "Profile", href: "#", current: true },
  { name: "Calendar", href: "#", current: false },
  { name: "Recognition", href: "#", current: false },
];

const team = [
  {
    name: "Leslie Alexander",
    handle: "lesliealexander",
    role: "Co-Founder / CEO",
    imageUrl:
      "https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80",
  },
  {
    name: "Michael Foster",
    handle: "michaelfoster",
    role: "Co-Founder / CTO",
    imageUrl:
      "https://images.unsplash.com/photo-1519244703995-f4e0f30006d5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80",
  },
  {
    name: "Dries Vincent",
    handle: "driesvincent",
    role: "Manager, Business Relations",
    imageUrl:
      "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80",
  },
  {
    name: "Lindsay Walton",
    handle: "lindsaywalton",
    role: "Front-end Developer",
    imageUrl:
      "https://images.unsplash.com/photo-1517841905240-472988babdf9?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80",
  },
];

export function classNames(...classes: string[]) {
  return classes.filter(Boolean).join(" ");
}

export function People({
  profile,
  directory,
  onUpdateEvent,
  onDeleteEvent,
  ...props
}: PeopleProps) {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [selectedTab, setSelectedTab] = useState(tabs[0].name);
  const fakeProps = {
    navigation: navigation,
    secondaryNavigation: secondaryNavigation,
    tabs: tabs,
  };
  return (
    <div className="relative h-screen flex overflow-hidden bg-white">
      <MobileSidebar
        {...fakeProps}
        {...props}
        sidebarOpen={sidebarOpen}
        setSidebarOpen={setSidebarOpen}
      />
      {/* Static sidebar for desktop */}
      <div className="hidden lg:flex lg:flex-shrink-0">
        <div className="flex flex-col w-64">
          <Sidebar {...fakeProps} {...props} />
        </div>
      </div>

      <div className="flex flex-col min-w-0 flex-1 overflow-hidden">
        <div className="lg:hidden">
          <div className="flex items-center justify-between bg-gray-50 border-b border-gray-200 px-4 py-1.5">
            <div>
              <img
                className="h-8 w-auto"
                src="https://tailwindui.com/img/logos/workflow-mark-pink-500.svg"
                alt="Workflow"
              />
            </div>
            <div>
              <button
                type="button"
                className="-mr-3 h-12 w-12 inline-flex items-center justify-center rounded-md text-gray-500 hover:text-gray-900 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-pink-600"
                onClick={() => setSidebarOpen(true)}
              >
                <span className="sr-only">Open sidebar</span>
                <MenuIcon className="h-6 w-6" aria-hidden="true" />
              </button>
            </div>
          </div>
        </div>
        <div className="flex-1 relative z-0 flex overflow-hidden">
          <main className="flex-1 relative z-0 overflow-y-auto focus:outline-none xl:order-last">
            <Breadcrumb />

            <article>
              <ProfileHeader profile={profile}></ProfileHeader>
              <Tabs
                tabs={tabs}
                selectedTab={selectedTab}
                setSelectedTab={setSelectedTab}
              />
              {selectedTab === "Profile" && (
                <>
                  <DescriptionList profile={profile}></DescriptionList>
                  <TeamMemberList team={team}></TeamMemberList>
                </>
              )}
              {selectedTab === "Calendar" && (
                <EventCalendar
                  events={profile?.holidays || []}
                  onDeleteEvent={onDeleteEvent}
                  onUpdateEvent={onUpdateEvent}
                />
              )}
            </article>
          </main>
          <DirectoryList directory={directory} />
        </div>
      </div>
    </div>
  );
}
