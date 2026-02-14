# FloatingCompanions (NeoForge 1.21.1)

Floating visual pets that orbit players.

## Current Features
- Server-authoritative pet assignment (`/floatingpet ...`)
- Client-side visual rendering only (no world entity, no gameplay hitbox)
- Persistent pet state across server restarts (`SavedData`)
- One implemented pet: `floatingcompanions:bee`

## Commands
- `/floatingpet list`
- `/floatingpet set bee`
- `/floatingpet clear`
- `/floatingpet set bee <target>` (permission level 2)
- `/floatingpet clear <target>` (permission level 2)

## Project Structure
- `src/main/java/me/figgnus/floatingcompanions/FloatingCompanionsMod.java`
  Mod entrypoint and event wiring.
- `src/main/java/me/figgnus/floatingcompanions/pet/`
  Pet type model + registry + server service.
- `src/main/java/me/figgnus/floatingcompanions/persistence/`
  World save persistence (`PetSaveData`).
- `src/main/java/me/figgnus/floatingcompanions/network/`
  Payload + payload registration + login sync hook.
- `src/main/java/me/figgnus/floatingcompanions/client/`
  Client bootstrap and render hooks.
- `src/main/java/me/figgnus/floatingcompanions/client/render/`
  Rendering API and per-pet renderer implementations.

## How It Works
1. Server stores `player UUID -> pet id` in `PetSaveData`.
2. Command changes update save data and broadcast state to clients.
3. On login, each client receives a full snapshot.
4. Client render event draws visual pet around each player with an active pet.

## Add a New Pet (Example Workflow)
1. Register a new pet id in `PetRegistry`:
   - `public static final PetType FOX = register(SimplePetType.create(id("fox")));`
2. Create a client renderer class in `client/render/`, implementing `PetVisualRenderer`.
3. Register renderer in `PetRenderRegistry.registerDefaults()`:
   - `register(PetRegistry.FOX.id(), new FoxPetRenderer());`
4. Add translation key in `en_us.json`:
   - `"pet.floatingcompanions.fox": "Floating Fox"`
5. Build and test:
   - `./gradlew build`

## Notes
- This system currently supports one active pet per player.
- Pet ids are suggested in command auto-complete.
- Client state is cleared when disconnecting to avoid cross-server stale visuals.
