#include "raylib.h"
#include <stdio.h>

// brew install raylib --HEAD
// gcc drift.c $(pkg-config --libs --cflags raylib) -o drift


int main(void) {
    InitWindow(400, 300, "test");
    SetTargetFPS(60);

    // Set axis deadzones:
    int dz = 0;
    float laxdz = 0.5f;
    float laydz = 0.5f;
    float raxdz = 0.5f;
    float raydz = 0.5f;

    int count = 0;

    while (!WindowShouldClose()) {

        // Toggle deadzone: [gamepad button A]
        if (IsGamepadButtonPressed(0, GAMEPAD_BUTTON_RIGHT_FACE_DOWN)) { dz = !dz; }

        // Get axis values:
        float lax = GetGamepadAxisMovement(0, GAMEPAD_AXIS_LEFT_X);
        float lay = GetGamepadAxisMovement(0, GAMEPAD_AXIS_LEFT_Y);
        float rax = GetGamepadAxisMovement(0, GAMEPAD_AXIS_RIGHT_X);
        float ray = GetGamepadAxisMovement(0, GAMEPAD_AXIS_RIGHT_Y);

        // Calculate deadzones:
        /* if (dz) { */
        /*     if (lax > -laxdz && lax < laxdz) { lax = 0.0f; } */
        /*     if (lay > -laydz && lay < laydz) { lay = 0.0f; } */
        /*     if (rax > -raxdz && rax < raxdz) { rax = 0.0f; } */
        /*     if (ray > -raydz && ray < raydz) { ray = 0.0f; } */
        /* } */

        BeginDrawing();
        ClearBackground(RAYWHITE);

        DrawCircle(100, 80, 50, LIGHTGRAY);
        if (dz) { DrawEllipse(100, 80, 50*laxdz, 50*laydz, BLACK); }
        DrawCircle(100+(int)(lax*50), 80+(int)(lay*50), 5, RED);
        DrawText(TextFormat("LAX %f", lax), 30, 140, 20, BLACK);
        DrawText(TextFormat("LAY %f", lay), 30, 160, 20, BLACK);

        DrawCircle(300, 80, 50, LIGHTGRAY);
        if (dz) { DrawEllipse(300, 80, 50*raxdz, 50*raydz, BLACK); }
        DrawCircle(300+(int)(rax*50), 80+(int)(ray*50), 5, BLUE);
        DrawText(TextFormat("RAX %f", rax), 230, 140, 20, BLACK);
        DrawText(TextFormat("RAY %f", ray), 230, 160, 20, BLACK);


        char buffer [500];
        sprintf(buffer, "Toggle deadzone: [gamepad button A] \n Count: %d, \n Gamepad: %s \n Gamepad available: %d",
                count, GetGamepadName(0),
                //IsGamepadAvailable(0)
                //
                IsKeyPressed(KEY_RIGHT)

                //IsGamepadButtonPressed(0, GAMEPAD_BUTTON_RIGHT_FACE_DOWN)
                );
        DrawText(buffer, 30, 220, 20, BLACK);
        EndDrawing();

        count++;
    }
    CloseWindow();
    return 0;
}
