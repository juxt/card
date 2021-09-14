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
import { MobileSidebar } from "./MobileSidebar";
import { Sidebar } from "./DesktopSidebar";
import { ProfileHeader } from "./ProfileHeader";
import { Tabs } from "./Tabs";
import { DescriptionList } from "./DescriptionList";
import { DirectoryList } from "./DirectoryList";
import { PeopleProps } from "../types";
import Breadcrumb from "./Breadcrumb";
import { EventCalendar } from "./Calendar";

const navigation = [
  { name: "Dashboard", href: "#", icon: HomeIcon, current: false },
  { name: "Calendar", href: "#", icon: CalendarIcon, current: false },
  { name: "Teams", href: "#", icon: UserGroupIcon, current: false },
  { name: "Directory", href: "#", icon: SearchCircleIcon, current: true },
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
];

export function classNames(...classes: string[]) {
  return classes.filter(Boolean).join(" ");
}

export function People({
  profile,
  directory,
  user,
  onUpdateEvent,
  onDeleteEvent,
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
        user={user}
        sidebarOpen={sidebarOpen}
        setSidebarOpen={setSidebarOpen}
      />
      {/* Static sidebar for desktop */}
      <div className="hidden lg:flex lg:flex-shrink-0">
        <div className="flex flex-col w-64">
          <Sidebar {...fakeProps} user={user} />
        </div>
      </div>

      <div className="flex flex-col min-w-0 flex-1 overflow-hidden">
        <div className="lg:hidden">
          <div className="flex items-center justify-between bg-gray-50 border-b border-gray-200 px-4 py-1.5">
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
            <Breadcrumb page={navigation.find((el) => el.current)!} />

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
                </>
              )}
              {selectedTab === "Calendar" && (
                <EventCalendar
                  events={profile?.holidays || []}
                  isCurrentUser={user?.id === profile?.id}
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
