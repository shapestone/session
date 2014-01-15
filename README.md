session
=======

The Session Service Manages a party session with an application. In the typical usage scenario an application will:
* Call the session service to create a session for a party.
* On every X request the application will check with the session service as to whether the session is still valid
* When configured/asked to the session service will encrypt/decript session data
* The session service can rate limit request
