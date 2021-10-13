import { People } from "./People";
import { Meta, Story } from "@storybook/react";
import { PeopleProps } from "../types";
import { MOCK_DIRECTORY, MOCK_NAV_PROPS, MOCK_PROJECTS } from "../utils";
import { NavBar } from "./Navbar";

export default {
  title: "People/dashboard",
  component: People,
  parameters: {
    actions: { argTypesRegex: "^on.*" },
    chromatic: { viewports: [320, 480, 1200, 1790] },
  },
} as Meta;

const Template: Story<PeopleProps> = (args) => (
  <div>
    <NavBar {...MOCK_NAV_PROPS} />
    <People {...args} />
  </div>
);

export const PeoplePage = Template.bind({});
PeoplePage.args = {
  isProfileLoading: false,
  isDirectoryLoading: false,
  profile: {
    name: "Mike Bruce",
    projects: MOCK_PROJECTS,
    id: "mic",
    imageUrl: "https://home.juxt.site/_site/users/mic/slack/mic.png",
    coverImageUrl:
      "https://images.unsplash.com/photo-1444628838545-ac4016a5418a?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80",
    about:
      "<p>Tincidunt quam neque in cursus viverra orci, dapibus nec tristique. Nullam ut sit dolor consectetur urna, dui cras nec sed. Cursus risus congue arcu aenean posuere aliquam.</p>\n   <p>Et vivamus lorem pulvinar nascetur non. Pulvinar a sed platea rhoncus ac mauris amet. Urna, sem pretium sit pretium urna, senectus vitae. Scelerisque fermentum, cursus felis dui suspendisse velit pharetra. Augue et duis cursus maecenas eget quam lectus. Accumsan vitae nascetur pharetra rhoncus praesent dictum risus suspendisse.</p>",
    fields: {
      Phone: "(555) 123-4567",
      Email: "mic@juxt.pro",
      Title: "Senior Front-End Developer",
      Team: "Product Development",
      Location: "San Francisco",
      Sits: "Oasis, 4th floor",
      Salary: "$145,000",
      Birthday: "June 8, 1990",
    },
  },
  isCurrentUser: true,
  directory: MOCK_DIRECTORY,
};

export const Loading = Template.bind({});
Loading.args = {
  isProfileLoading: true,
  isDirectoryLoading: true,
};
