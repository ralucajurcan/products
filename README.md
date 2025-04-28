## Activity One Lifecycle

The MainActivity has an activity-scoped ViewModel shared by three fragments (Fragment1, Fragment2, Fragment3).
Each fragment shares data through the shared ViewModel.
All fragment lifecycle functions (`onCreate`, `onCreateView`, `onViewCreated`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy`, `onDetach`) are logged for debugging and observation.

## Activity Two Lifecycle

ActivityTwo includes six fragments (Fragment1 to Fragment6).
Fragments F2 to F5 are part of a Navigation Graph (`navGraph`).
Fragments F2, F3, and F4 share a ViewModel scoped to the navGraph and share data between each other.
Fragment6 is outside of the navGraph and is managed manually.
