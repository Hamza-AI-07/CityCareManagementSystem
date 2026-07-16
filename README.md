# City Care

City Care is an Android-based municipal grievance management system for citizens, administrators, and departmental officers. It lets residents register complaints about city services, attach photos, add location details, track complaint progress, and submit feedback after resolution. Administrators can manage users, route complaints to departments, update status, add remarks, and view summary dashboards. Officers can review complaints assigned to their department and monitor workload metrics.

## Project Summary

This app is built with Java on Android and uses Firebase for authentication, realtime data storage, and file uploads. It also integrates Google Maps, device camera and gallery access, and image upload support for complaint evidence.

## Core Capabilities

- Citizen sign-up and login
- Role-based access for User, Admin, and Officer accounts
- Complaint creation with category selection
- Optional complaint image upload from camera or gallery
- Location capture and map viewing
- Complaint listing, filtering, search, and detail views
- Complaint editing and withdrawal by the original reporter
- Feedback voting after complaint handling
- Admin dashboards for complaint statistics and date filtering
- Admin user/officer management and complaint routing
- Officer dashboard for department-specific workload tracking

## Application Flow

### Startup Flow

1. The app launches from the splash screen defined in `splash_sucreen`.
2. The splash screen redirects to `MainActivity` after a short delay.
3. `MainActivity` initializes Firebase and sends the user to `LoginActivity`.
4. If a user session already exists, the app redirects to the correct dashboard based on the saved role.

### Authentication Flow

The login screen supports two main modes:

- User login
- Admin/Officer login

The app checks the authenticated email and role stored in Firebase Realtime Database to decide where to send the user:

- User -> `UserHomeTab`
- Admin -> `AdminPanelActivity`
- Officer/department role -> `OfficerDashboardActivity`

### Registration Flow

Citizens can register through `SignUpActivity`.

1. The user enters name, email, password, and confirmation password.
2. The app validates the form fields.
3. A Firebase Authentication account is created.
4. The user profile is stored in Firebase Realtime Database under the `Users` node.
5. The user is redirected back to the login screen.

## Role-Based Workflows

### Citizen / User Workflow

Users work primarily through `UserHomeTab`, `UserHomeActivity`, and the complaint list/detail screens.

Typical flow:

1. Log in as a user.
2. Open the home tab to see complaint and feedback summary cards.
3. Tap the add button to create a new complaint.
4. Choose a complaint category such as cleanliness, street lights, water connection, illegal construction, encroachment, road repair, or graveyards.
5. Fill in the complaint form:
   - name
   - phone number
   - email
   - title
   - description
   - address
   - latitude and longitude
6. Attach evidence using the camera or gallery if needed.
7. Submit the complaint.
8. View all submitted complaints in category lists or the full complaint list.
9. Open a complaint to see status, assigned role, remarks, and the map location.
10. Optionally edit the complaint, withdraw it, or leave positive/negative feedback after progress is updated.

User-specific screens include:

- `NewComplaintActivity`
- `CreateNewComplaintActivity`
- `AllComplaintsListUserActivity`
- `ShowAllComplaintsUserActivity`
- `ComplaintDetailsUserActivity`
- feedback category screens such as `PositiveFeedbackActivity`, `NegativeFeedbackActivity`, `PendingFeedbackActivity`, and `DroppedFeedbackActivity`

### Admin Workflow

Administrators use `AdminPanelActivity`, `AdminDashboardActivity`, and `AdminHomeActivity`.

Typical flow:

1. Log in with the admin account.
2. Open the admin panel.
3. Review the complaint dashboard and summary counts.
4. Filter complaint statistics by date range.
5. Open complaint categories to inspect complaints grouped by type.
6. Open complaint details to:
   - forward a complaint to a department role
   - add or update remarks
   - set status to pending, open, in progress, closed, or rejected
   - delete complaints when needed
7. Manage officer credentials from the officer management screen.
8. View or update profile data in the admin home screen.

Admin-specific screens include:

- `AdminPanelActivity`
- `AdminDashboardActivity`
- `AdminHomeActivity`
- `ManageUsersActivity`
- `AllComplaintsActivity`
- `SortedComplaintsListActivity`
- `ComplaintDetailsAdminActivity`

### Officer Workflow

Officers use `OfficerDashboardActivity` and the complaint detail/list screens.

Typical flow:

1. Log in using an officer or department account.
2. Open the officer dashboard.
3. Review the number of complaints assigned to the officer’s role.
4. View counts for pending, in progress, closed, rejected, positive feedback, and negative feedback.
5. Open assigned complaint lists to inspect details, maps, and complaint history.

The app treats any non-user, non-admin role as a department role. Complaint lists and details are filtered by the logged-in role so officers only see complaints assigned to their department.

## Complaint Lifecycle

The complaint workflow is built around the `Complaints` node in Firebase Realtime Database.

1. A user creates a complaint with a unique complaint ID.
2. The complaint is stored with a default status of `pending`.
3. An admin reviews the complaint in the dashboard or category list.
4. The admin can assign the complaint to a department role.
5. The complaint status can move through:
   - `pending`
   - `open`
   - `in progress`
   - `closed`
   - `rejected`
6. The user can see the assigned role and remarks in the complaint details screen.
7. Once the complaint is handled, the citizen can mark feedback as positive or negative.
8. Complaints can be edited or withdrawn by the original reporter.

## Data Model Overview

### Firebase Realtime Database

The app uses these main nodes:

- `Users`
- `Complaints`
- `admin`

### User Model

A user record stores:

- name
- email
- password
- image URL
- role

### Complaint Model

A complaint record stores:

- complaint ID
- complainer name
- complainer phone number
- complainer email
- complaint title
- complaint description
- complainer address
- latitude and longitude
- complaint category
- complaint image URL
- complaint status
- assigned role
- remarks
- feedback
- likes and dislikes
- timestamp
- visibility preference for the reporter’s phone number

## Firebase and External Services

The app relies on the following services:

- Firebase Authentication for login and account creation
- Firebase Realtime Database for users, complaints, roles, and complaint state
- Firebase Storage for complaint images and profile media
- Google Maps for location viewing
- Google Play Services Location and Places APIs for location-related features

## Permissions

The app requests permissions for:

- internet access
- camera access
- gallery and image reading access
- fine and coarse location
- storage-related access on older Android versions
- wake lock and account-related permissions used by included SDKs

## Tech Stack

- Java
- Android SDK 34
- Firebase BoM 34.14.0
- Firebase Authentication
- Firebase Realtime Database
- Firebase Storage
- Google Play Services Maps, Location, and Places
- AndroidX libraries
- Glide and Picasso for image handling
- RecyclerView, CardView, Material Components

## Project Structure

- `app/src/main/java/com/example/municipalservices/` contains activities, fragments, adapters, models, and utilities.
- `app/src/main/res/layout/` contains screen layouts.
- `app/src/main/res/drawable/` contains icons and UI assets.
- `app/src/main/res/values/` contains strings, colors, and theme resources.
- `app/google-services.json` configures Firebase for the Android app.

## Local Setup

### Prerequisites

- Android Studio
- JDK 17
- A Firebase project connected to the app
- A Google Maps API key configured in Firebase or string resources

### Setup Steps

1. Clone the repository.
2. Open the project in Android Studio.
3. Make sure `google-services.json` matches your Firebase project.
4. Confirm the Firebase Realtime Database and Storage rules are set correctly.
5. Update the storage bucket constant if your Firebase Storage bucket name differs.
6. Sync Gradle.
7. Run the app on a device or emulator.

### Build Commands

```bash
./gradlew assembleDebug
./gradlew test
```

On Windows PowerShell, use:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat test
```

## Notes

- The app currently uses role-based branching from the login screen rather than a separate navigation framework.
- Complaint filtering and dashboards depend on Firebase data being populated correctly.
- Some screens are designed around the current database structure, so changing node names will require code updates.

## Repository

This repository is published as `CityCareManagementSystem`.
