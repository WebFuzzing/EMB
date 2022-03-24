const dotenv = require("dotenv");
const fs = require("fs-extra");
const path = require("path");
const stringifyCsv = require("csv-stringify/lib/sync"); // TODO: Use the async version instead?
const parseCsv = require("csv-parse/lib/sync"); // TODO: Use the async version instead?
const bent = require("bent");

// FIXME: Resolve against project root, not module as this is brittle
dotenv.config({
  path: path.resolve(__dirname, "../.env"),
});

generateDiversityCoupons();

async function generateDiversityCoupons() {
  const diversitySelection = parseCsv(
    await fs.readFile("./diversity-tickets.csv", { encoding: "utf8" }),
    {
      columns: true,
      delimiter: ",",
      skip_empty_lines: true,
    }
  );
  const selectionWithDiscountCodes = generateDiscountCodes(diversitySelection);

  selectionWithDiscountCodes.forEach(selected => {
    createDiversityTicket(selected.DiversityCoupon);
  });

  const stringifiedSelection = stringifyCsv(selectionWithDiscountCodes, {
    header: true,
  });

  console.log(stringifiedSelection);
}

function generateDiscountCodes(diversitySelection) {
  return diversitySelection.map(selected => ({
    ...selected,
    DiversityCoupon: `DIVERSITY_${selected.Name.split(" ")
      .join("")
      .toUpperCase()}`,
  }));
}

async function createDiversityTicket(code) {
  const titoToken = process.env.TITO_TOKEN;
  const titoApi = titoClient(titoToken, "react-finland", "2019");
  // const releases = await titoApi.getReleases();
  const diversityReleaseId = "id0xabhbxjo"; // TODO: Better to search from the releases

  return await titoApi.createDiscountCode({
    code,
    type: "PercentOffDiscountCode",
    value: 100,
    quantity: 1,
    releaseIds: [diversityReleaseId],
    revealSecret: true,
    onlyShowAttached: true,
  });
}

function titoClient(authToken, accountId, eventId) {
  const auth = {
    Authorization: `Token token=${authToken}`,
    "Content-Type": "application/json",
  };
  const getJson = bent("json", auth);
  const postJson = bent("POST", 201, auth);

  return {
    async getReleases() {
      return (await getJson(getTitoEndPoint("releases"))).data;
    },
    async getRelease(id) {
      return (await getJson(getTitoEndPoint(`releases/${id}`))).data;
    },
    async getDiscountCodes() {
      return (await getJson(getTitoEndPoint("discount_codes"))).data;
    },
    async getDiscountCode(id) {
      return (await getJson(getTitoEndPoint(`discount_codes/${id}`))).data;
    },
    async createDiscountCode({
      code,
      type,
      value,
      quantity,
      onlyShowAttached,
      releaseIds,
      revealSecret,
    }) {
      await postJson(getTitoEndPoint("discount_codes"), {
        data: {
          type: "discount-codes",
          attributes: {
            code, // DISCOUNTDRACULA
            type, // MoneyOffDiscountCode/PercentOffDiscountCode
            value, // 25.00
            quantity,
            "release-ids": releaseIds,
            "reveal-secret": revealSecret,
            "only-show-attached": onlyShowAttached,
          },
        },
      });

      // If the code throws, then we won't return code. I am not sure what's the best way yet.
      return code;
    },
  };

  function getTitoEndPoint(endPointName) {
    return `https://api.tito.io/v2/${accountId}/${eventId}/${endPointName}`;
  }
}
