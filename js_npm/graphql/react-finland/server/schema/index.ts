import { buildSchema, registerEnumType } from "type-graphql";
import { ContactType } from "./Contact";
import * as resolvers from "./resolvers";
import { Level, SessionType } from "./Session";

registerEnumType(ContactType, {
  name: "ContactType",
  description: "Type of the contact",
});

registerEnumType(SessionType, {
  name: "SessionType",
  description: "Type of the session",
});

registerEnumType(Level, {
  name: "Level",
  description: "Level of the session",
});

export default async function generateSchema() {
  return await buildSchema({
    resolvers: Object.values(resolvers),
    // scalarsMap: [{ type: Url, scalar: UrlScalar }],
    // TODO: Emitting the schema could be useful for query checks.
    // emitSchemaFile: true, // use path for a specific location
  });
}
