The JSON files in this directory are real output from Assemblyline for
al_test.txt and al_test.zip. The JSON files will be used for testing
deserialization of the JSON by this client. Past experience has shown that
trying to manually tweak Assemblyline responses to match imaginary files is
difficult and error prone.

Fields that should probably be scrubbed before committing new JSON:
- usernames

Notes:
- The java code that creates expected deserializations of the JSON re-uses
structures for multiple JSON files because a lot of the JSON responses are
composed of objects from other responses. However, in some cases, fetching a
response from Assemblyline changes fields, even if the data has not otherwise
changed. For example, the timestamps for result blocks (archive_ts, created,
expiry_ts, response.milestones.service_completed,
response.milestones.service_started) apparently change every time they are
fetched, so they will be different when fetched via
/submission/{sid}/file/{sha256} and /submission/full/{sid} (or even multiple
executions of the same endpoint). The current fix is to edit the timestamps in
one response or the other to match.