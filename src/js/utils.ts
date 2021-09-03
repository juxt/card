import { EventInput } from "@fullcalendar/react";
import { useEffect, useState } from "react";
import { User } from "./types";

export function classNames(...classes: string[]) {
  return classes.filter(Boolean).join(" ");
}

function uuidv4() {
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
    var r = (Math.random() * 16) | 0,
      v = c == "x" ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

let todayStr = new Date().toISOString().replace(/T.*$/, ""); // YYYY-MM-DD of today

export const MOCK_EVENTS: EventInput[] = [
  {
    id: createEventId(),
    title: "All-day event",
    start: todayStr,
    allDay: true,
    end: todayStr + "T23:00:00",
  },
  {
    id: createEventId(),
    title: "Timed event",
    start: todayStr + "T12:00:00",
    end: todayStr + "T14:30:00",
  },
];

export const MOCK_USER: User = {
  id: "1",
  email: "alx@juxt.pro",
  fields: {},
  name: "Alex Davis",
  imageUrl: "https://ca.slack-edge.com/T02AJV0T3-U7KDWJTT6-500d11650fe2-512",
};

export function createEventId() {
  return uuidv4();
}

export default function useMobileDetect(): boolean {
  const [isMobile, setIsMobile] = useState(window.innerWidth < 640);

  function handleSizeChange(): void {
    return setIsMobile(window.innerWidth < 640);
  }

  useEffect(() => {
    window.addEventListener("resize", handleSizeChange);

    return () => {
      window.removeEventListener("resize", handleSizeChange);
    };
  }, [isMobile]);

  return isMobile;
}

export const MOCK_LOGO = "https://home.juxt.site/x-on-dark.svg";

export const MOCK_NAVIGATION = [
  {
    id: "1",
    name: "Home",
    href: "",
    current: false,
  },
  {
    id: "2",
    name: "About",
    href: "",
    current: false,
  },
  {
    id: "3",
    name: "People",
    href: "",
    current: true,
  },
];

export const MOCK_NAV_PROPS = {
  user: MOCK_USER,
  navigation: MOCK_NAVIGATION,
  logo: MOCK_LOGO,
};