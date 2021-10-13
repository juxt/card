import { useEffect, useState } from "react";
import { CalendarFormData, Directory, User } from "./types";

export function classNames(...classes: Array<string | undefined>) {
  return classes.filter(Boolean).join(" ");
}

function uuidv4() {
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0,
      v = c == "x" ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}
const today = new Date();
const dateStr = (date: Date) => date.toISOString().replace(/T.*$/, ""); // YYYY-MM-DD of today
const daysPlus = (n: number) =>
  new Date(today.getTime() + n * 24 * 60 * 60 * 1000);

export const MOCK_PROJECTS = [
  {
    label: "Project 1",
    value: "p1",
  },
  {
    label: "Project 2",
    value: "p2",
  },
  {
    label: "Project 3",
    value: "p3",
  },
];

export const MOCK_DIRECTORY: Directory = {
  T: [
    {
      name: "Jack Tolley",
      id: "jck",
      projects: [
        {
          label: "Project 1",
          value: "p1",
        },
        {
          label: "Project 2",
          value: "p2",
        },
      ],
      imageUrl: "https://home.juxt.site/_site/users/jck/slack/jck.jpg",
      coverImageUrl:
        "https://images.unsplash.com/photo-1444628838545-ac4016a5418a?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80",
      about:
        "<p>Tincidunt quam neque in cursus viverra orci, dapibus nec tristique. Nullam ut sit dolor consectetur urna, dui cras nec sed. Cursus risus congue arcu aenean posuere aliquam.</p>\n   <p>Et vivamus lorem pulvinar nascetur non. Pulvinar a sed platea rhoncus ac mauris amet. Urna, sem pretium sit pretium urna, senectus vitae. Scelerisque fermentum, cursus felis dui suspendisse velit pharetra. Augue et duis cursus maecenas eget quam lectus. Accumsan vitae nascetur pharetra rhoncus praesent dictum risus suspendisse.</p>",
      fields: {
        Phone: "(555) 123-4567",
        Email: "jck@juxt.pro",
        Title: "Senior Front-End Developer",
        Team: "Product Development",
        Location: "San Francisco",
        Sits: "Oasis, 4th floor",
        Salary: "$145,000",
        Birthday: "June 8, 1990",
      },
    },
    {
      name: "Jeremy Taylor",
      projects: MOCK_PROJECTS,
      id: "jdt",
      imageUrl: "https://home.juxt.site/_site/users/jdt/slack/jdt.jpg",
      coverImageUrl:
        "https://images.unsplash.com/photo-1444628838545-ac4016a5418a?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80",
      about:
        "<p>Tincidunt quam neque in cursus viverra orci, dapibus nec tristique. Nullam ut sit dolor consectetur urna, dui cras nec sed. Cursus risus congue arcu aenean posuere aliquam.</p>\n   <p>Et vivamus lorem pulvinar nascetur non. Pulvinar a sed platea rhoncus ac mauris amet. Urna, sem pretium sit pretium urna, senectus vitae. Scelerisque fermentum, cursus felis dui suspendisse velit pharetra. Augue et duis cursus maecenas eget quam lectus. Accumsan vitae nascetur pharetra rhoncus praesent dictum risus suspendisse.</p>",
      fields: {
        Phone: "(555) 123-4567",
        Email: "jdt@juxt.pro",
        Title: "Senior Front-End Developer",
        Team: "Product Development",
        Location: "San Francisco",
        Sits: "Oasis, 4th floor",
        Salary: "$145,000",
        Birthday: "June 8, 1990",
      },
    },
  ],
};

export const MOCK_EVENTS: CalendarFormData[] = [
  {
    id: createEventId(),
    title: "All-day event",
    start: dateStr(today),
    allDay: true,
    end: dateStr(daysPlus(1)),
    type: "Holiday",
  },
  {
    id: createEventId(),
    title: "Really long name timed event to test truncation",
    start: dateStr(today) + "T10:00:00",
    end: dateStr(today) + "T11:30:00",
    type: "Timesheet",
  },
  {
    id: createEventId(),
    allDay: true,
    title: "Half day test",
    start: dateStr(daysPlus(1)),
    isStartHalfDay: true,
    end: dateStr(daysPlus(4)),
    isEndHalfDay: true,
    type: "Holiday",
  },
  {
    id: createEventId(),
    title: "Timed event",
    start: dateStr(today) + "T12:00:00",
    end: dateStr(today) + "T14:30:00",
    type: "Timesheet",
  },
];

export const MOCK_USER: User = {
  id: "1",
  email: "alx@juxt.pro",
  fields: {},
  projects: MOCK_PROJECTS,
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
    path: "",
    current: false,
  },
  {
    id: "2",
    name: "About",
    path: "",
    current: false,
  },
  {
    id: "3",
    name: "People",
    path: "",
    current: true,
  },
];

export const MOCK_NAV_PROPS = {
  user: MOCK_USER,
  navigation: MOCK_NAVIGATION,
  logo: MOCK_LOGO,
};
