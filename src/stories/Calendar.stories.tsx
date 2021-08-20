import { CalendarProps, BasicCalendar } from "./Calendar";
import { Meta, Story } from "@storybook/react";

export default {
  title: "People/Calendar",
  component: BasicCalendar,
} as Meta;

const Template: Story<CalendarProps> = (args) => <BasicCalendar {...args} />;
const now = new Date();
export const Calendar = Template.bind({});
Calendar.args = {
  initialEvents: [
    {
      title: "All Day Event very long title",
      allDay: true,
      start: new Date(2021, 8, 0),
      end: new Date(2021, 8, 1),
    },
    {
      title: "Long Event",
      start: new Date(2021, 8, 7),
      end: new Date(2021, 8, 10),
    },

    {
      title: "DTS STARTS",
      start: new Date(2016, 2, 13, 0, 0, 0),
      end: new Date(2016, 2, 20, 0, 0, 0),
    },

    {
      title: "DTS ENDS",
      start: new Date(2016, 10, 6, 0, 0, 0),
      end: new Date(2016, 10, 13, 0, 0, 0),
    },

    {
      title: "Some Event",
      start: new Date(2021, 8, 9, 0, 0, 0),
      end: new Date(2021, 8, 10, 0, 0, 0),
    },
    {
      title: "Conference",
      start: new Date(2021, 8, 11),
      end: new Date(2021, 8, 13),
      desc: "Big conference for important people",
    },
    {
      title: "Meeting",
      start: new Date(2021, 8, 12, 10, 30, 0, 0),
      end: new Date(2021, 8, 12, 12, 30, 0, 0),
      desc: "Pre-meeting meeting, to prepare for the meeting",
    },
    {
      title: "Lunch",
      start: new Date(2021, 8, 12, 12, 0, 0, 0),
      end: new Date(2021, 8, 12, 13, 0, 0, 0),
      desc: "Power lunch",
    },
    {
      title: "Meeting",
      start: new Date(2021, 8, 12, 14, 0, 0, 0),
      end: new Date(2021, 8, 12, 15, 0, 0, 0),
    },
    {
      title: "Happy Hour",
      start: new Date(2021, 8, 12, 17, 0, 0, 0),
      end: new Date(2021, 8, 12, 17, 30, 0, 0),
      desc: "Most important meal of the day",
    },
    {
      title: "Dinner",
      start: new Date(2021, 8, 12, 20, 0, 0, 0),
      end: new Date(2021, 8, 12, 21, 0, 0, 0),
    },
    {
      title: "Planning Meeting with Paige",
      start: new Date(2021, 8, 13, 8, 0, 0),
      end: new Date(2021, 8, 13, 10, 30, 0),
    },
    {
      title: "Inconvenient Conference Call",
      start: new Date(2021, 8, 13, 9, 30, 0),
      end: new Date(2021, 8, 13, 12, 0, 0),
    },
    {
      title: "Project Kickoff - Lou's Shoes",
      start: new Date(2021, 8, 13, 11, 30, 0),
      end: new Date(2021, 8, 13, 14, 0, 0),
    },
    {
      title: "Quote Follow-up - Tea by Tina",
      start: new Date(2021, 8, 13, 15, 30, 0),
      end: new Date(2021, 8, 13, 16, 0, 0),
    },
    {
      title: "Late Night Event",
      start: new Date(2021, 8, 17, 19, 30, 0),
      end: new Date(2021, 8, 18, 2, 0, 0),
    },
    {
      title: "Late Same Night Event",
      start: new Date(2021, 8, 17, 19, 30, 0),
      end: new Date(2021, 8, 17, 23, 30, 0),
    },
    {
      title: "Multi-day Event",
      start: new Date(2021, 8, 20, 19, 30, 0),
      end: new Date(2021, 8, 22, 2, 0, 0),
    },
    {
      title: "Today",
      start: new Date(new Date().setHours(new Date().getHours() - 3)),
      end: new Date(new Date().setHours(new Date().getHours() + 3)),
    },
    {
      title: "Point in Time Event",
      start: now,
      end: now,
    },
    {
      title: "Video Record",
      start: new Date(2021, 8, 14, 15, 30, 0),
      end: new Date(2021, 8, 14, 19, 0, 0),
    },
    {
      title: "Dutch Song Producing",
      start: new Date(2021, 8, 14, 16, 30, 0),
      end: new Date(2021, 8, 14, 20, 0, 0),
    },
    {
      title: "Itaewon Halloween Meeting",
      start: new Date(2021, 8, 14, 16, 30, 0),
      end: new Date(2021, 8, 14, 17, 30, 0),
    },
    {
      title: "Online Coding Test",
      start: new Date(2021, 8, 14, 17, 30, 0),
      end: new Date(2021, 8, 14, 20, 30, 0),
    },
    {
      title: "An overlapped Event",
      start: new Date(2021, 8, 14, 17, 0, 0),
      end: new Date(2021, 8, 14, 18, 30, 0),
    },
    {
      title: "Phone Interview",
      start: new Date(2021, 8, 14, 17, 0, 0),
      end: new Date(2021, 8, 14, 18, 30, 0),
    },
    {
      title: "Cooking Class",
      start: new Date(2021, 8, 14, 17, 30, 0),
      end: new Date(2021, 8, 14, 19, 0, 0),
    },
    {
      title: "Go to the gym",
      start: new Date(2021, 8, 14, 18, 30, 0),
      end: new Date(2021, 8, 14, 20, 0, 0),
    },
  ],
};

export const CalendarNoEvents = Template.bind({});
CalendarNoEvents.args = {
  initialEvents: [],
};
