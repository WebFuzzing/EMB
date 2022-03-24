import React from "react";

interface ContactsProps {
  items: Array<{}>;
  render: any; // TODO: What's the correct type for this?
  renderProps: {};
}

const Contacts = ({ items = [], render, renderProps = {} }: ContactsProps) => (
  <>
    {items.map((contact, key) =>
      React.createElement(render, {
        ...contact,
        ...renderProps,
        key,
      })
    )}
  </>
);

export default Contacts;
