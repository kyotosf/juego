const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ISABELLA_UID = "isabella_uid_1";
const OTHER_UID = "other_uid_2";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validCreatePayload(uid, slotNumber = 1, now = new Date(Date.now() - 1000)) {
  return {
    userId: uid,
    slotNumber,
    slotName: "Historia de Isabella — Ranura " + slotNumber,
    currentChapter: 1,
    currentSceneIndex: 0,
    bondScore: 55,
    couragePoints: 5,
    tendernessPoints: 10,
    witPoints: 5,
    unlockedMemories: ["mem_queens_rose"],
    choiceHistory: ["ch1_c1"],
    playTimeSeconds: 42,
    finished: false,
    lastChapterTitle: "Cap. 1: Atardecer en Queens",
    createdAt: now,
    updatedAt: now,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write saveSlots", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ISABELLA_UID).collection("saveSlots").get());
  await assertFails(
    unauthDb.collection("users").doc(ISABELLA_UID).collection("saveSlots").doc("slot_1").set(
      validCreatePayload(ISABELLA_UID)
    )
  );
});

test("Owner can list empty collection, create valid save slot, partial-update, and delete", async () => {
  const db = testEnv.authenticatedContext(ISABELLA_UID).firestore();
  const slotsCol = db.collection("users").doc(ISABELLA_UID).collection("saveSlots");

  // Empty list query succeeds
  await assertSucceeds(slotsCol.get());

  // Valid create succeeds
  await assertSucceeds(slotsCol.doc("slot_1").set(validCreatePayload(ISABELLA_UID, 1)));

  // Valid partial update succeeds
  await assertSucceeds(
    slotsCol.doc("slot_1").update({
      currentChapter: 2,
      currentSceneIndex: 1,
      bondScore: 72,
      tendernessPoints: 20,
      unlockedMemories: ["mem_queens_rose", "mem_rooftop_lights"],
      choiceHistory: ["ch1_c1", "ch2_c1"],
      playTimeSeconds: 185,
      lastChapterTitle: "Cap. 2: La Azotea Secreta",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // List populated collection succeeds
  await assertSucceeds(slotsCol.get());

  // Delete succeeds
  await assertSucceeds(slotsCol.doc("slot_1").delete());
});

test("Rejects @DocumentId 'id' ghost field, future timestamps, out-of-range scores, and immutable field mutation", async () => {
  const db = testEnv.authenticatedContext(ISABELLA_UID).firestore();
  const slotsCol = db.collection("users").doc(ISABELLA_UID).collection("saveSlots");

  // Reject ghost 'id' field
  await assertFails(
    slotsCol.doc("slot_2").set({
      ...validCreatePayload(ISABELLA_UID, 2),
      id: "slot_2",
    })
  );

  // Reject future timestamp
  await assertFails(
    slotsCol.doc("slot_2").set(validCreatePayload(ISABELLA_UID, 2, new Date(Date.now() + 60000)))
  );

  // Reject out-of-bounds bondScore
  await assertFails(
    slotsCol.doc("slot_2").set({
      ...validCreatePayload(ISABELLA_UID, 2),
      bondScore: 150,
    })
  );

  // Create valid slot_1, then reject immutable createdAt / slotNumber mutation
  await assertSucceeds(slotsCol.doc("slot_1").set(validCreatePayload(ISABELLA_UID, 1)));
  await assertFails(
    slotsCol.doc("slot_1").update({
      createdAt: new Date(Date.now() - 100),
      updatedAt: new Date(Date.now() - 100),
    })
  );
  await assertFails(
    slotsCol.doc("slot_1").update({
      slotNumber: 3,
      updatedAt: new Date(Date.now() - 100),
    })
  );
});

test("Cross-user isolation: another authenticated user cannot read, write, or delete Isabella's saveSlots", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ISABELLA_UID).collection("saveSlots").doc("slot_1")
      .set(validCreatePayload(ISABELLA_UID, 1));
  });

  const otherDb = testEnv.authenticatedContext(OTHER_UID).firestore();
  const isabellaSlots = otherDb.collection("users").doc(ISABELLA_UID).collection("saveSlots");

  await assertFails(isabellaSlots.get());
  await assertFails(isabellaSlots.doc("slot_1").get());
  await assertFails(isabellaSlots.doc("slot_1").update({ bondScore: 100, updatedAt: new Date(Date.now() - 500) }));
  await assertFails(isabellaSlots.doc("slot_1").delete());
  await assertFails(isabellaSlots.doc("slot_2").set(validCreatePayload(ISABELLA_UID, 2)));
});
