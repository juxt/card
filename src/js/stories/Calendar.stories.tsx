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
      id: "1",
    },
    {
      title: "Long Event",
      start: new Date(2021, 8, 7),
      end: new Date(2021, 8, 10),
      id: "2",
    },
  ],
  onCreateEvent: (event) => {
    console.log("Event Created!", event);
  },
};

export const CalendarNoEvents = Template.bind({});
CalendarNoEvents.args = {
  initialEvents: [],
};