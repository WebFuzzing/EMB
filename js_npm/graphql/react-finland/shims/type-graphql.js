const dummyFn = () => void 0;
const dummyDecorator = () => dummyFn;

TypeGraphQL = {
  Arg: dummyDecorator,
  Args: dummyDecorator,
  ArgsType: dummyDecorator,
  Authorized: dummyDecorator,
  Ctx: dummyDecorator,
  registerEnumType: dummyFn,
  Field: typeFn => (fieldInstance, field) => {
    const metaField = "_fields";
    // It should be possible to write to the meta field but
    // not to enumerate it since it should be hidden.
    Object.defineProperty(fieldInstance.__proto__, metaField, {
      enumerable: false,
      writable: true,
    });

    // Create the meta field if it doesn't exist yet.
    const fields = fieldInstance.__proto__[metaField] || {};
    let type = typeFn();
    let values;

    if (!fieldInstance.__proto__[metaField]) {
      fieldInstance.__proto__[metaField] = {};
    }

    // Handle enums.
    if (typeof type === "object") {
      values = Object.keys(type); // TODO: Is this correct always?
      type = "enum";
    }

    // TODO: How to deal with scalars? Example: Url. Maybe run serialize?

    const existingFields =
      fieldInstance.__proto__[metaField][fieldInstance.constructor.name] || {};

    fieldInstance.__proto__[metaField] = {
      ...fields,
      [fieldInstance.constructor.name]: {
        ...existingFields,
        [field]: {
          type,
          default: "",
          values,
          from: [fieldInstance.constructor.name],
        },
      },
    };
  },
  FieldResolver: dummyDecorator,
  Info: dummyDecorator,
  InputType: dummyDecorator,
  InterfaceType: dummyDecorator,
  Mutation: dummyDecorator,
  ObjectType: dummyDecorator,
  PubSub: dummyDecorator,
  Query: dummyDecorator,
  Resolver: dummyDecorator,
  Root: dummyDecorator,
  Subscription: dummyDecorator,
  createUnionType: dummyFn,
  UseMiddleware: dummyDecorator,
};
