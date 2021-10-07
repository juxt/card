import { useState } from "react";
import { ChevronLeftIcon } from "@heroicons/react/outline";
import { ProfileHeader } from "./ProfileHeader";
import { Tabs } from "./Tabs";
import { DescriptionList } from "./DescriptionList";
import { DirectoryList } from "./DirectoryList";
import { PeopleProps } from "../types";
import { EventCalendar } from "./Calendar";
import { Disclosure, Transition } from "@headlessui/react";
import { useWindowSize } from "../hooks/useWindowSize";

const tabs = [
  { name: "Profile", path: "#", current: true },
  { name: "Calendar", path: "#", current: false },
];

export function People({
  profile,
  isProfileLoading,
  directory,
  isDirectoryLoading,
  user,
  onUpdateEvent,
  onDeleteEvent,
}: PeopleProps) {
  const [selectedTab, setSelectedTab] = useState(tabs[0].name);
  const size = useWindowSize();
  const isMobile = size?.width && size.width < 768;
  return (
    <div className="relative h-screen flex overflow-hidden bg-white">
      <Disclosure
        as="div"
        defaultOpen
        className="flex-1 relative z-0 flex overflow-hidden"
      >
        {({ open, close }) => (
          <>
            <Transition
              show={open}
              enter="transition ease-out duration-200 transform"
              enterFrom="-translate-x-40"
              enterTo="translate-x-0"
              leave="transition ease-in duration-200 transform"
              leaveFrom="translate-x-0"
              leaveTo="-translate-x-40"
            >
              <Disclosure.Panel
                as={DirectoryList}
                static
                directory={directory}
                isLoading={isDirectoryLoading}
              ></Disclosure.Panel>
            </Transition>
            <main
              onClick={() => isMobile && open && close()}
              style={{ zIndex: -1 }}
              className="flex-1 relative overflow-y-auto focus:outline-none xl:order-last"
            >
              {!open && (
                <Disclosure.Button
                  className="flex items-start px-4 py-3 sm:px-6 md:px-8"
                  aria-label="Open directory list"
                >
                  <ChevronLeftIcon
                    className="-ml-2 h-5 w-5 text-gray-400"
                    aria-hidden="true"
                  />
                  <span className="inline-flex items-center space-x-3 text-sm font-medium text-gray-900">
                    Show directory
                  </span>
                </Disclosure.Button>
              )}
              <article>
                <ProfileHeader
                  profile={profile}
                  isLoading={isProfileLoading}
                ></ProfileHeader>
                <Tabs
                  tabs={tabs}
                  selectedTab={selectedTab}
                  setSelectedTab={setSelectedTab}
                />
                {selectedTab === "Profile" && (
                  <DescriptionList
                    profile={profile}
                    isLoading={isProfileLoading}
                  />
                )}
                {selectedTab === "Calendar" && (
                  <EventCalendar
                    events={profile?.holidays || []}
                    projectOptions={profile?.projects || []}
                    isCurrentUser={user?.id === profile?.id}
                    onDeleteEvent={onDeleteEvent}
                    onUpdateEvent={onUpdateEvent}
                  />
                )}
              </article>
            </main>
          </>
        )}
      </Disclosure>
    </div>
  );
}
