# ExtractFacts
Used to extract facts from a given json file

Program inputs:
Format : JSON

paragraph:
String – paragraph of English text
Example:
{ paragraph: "Ram downloaded the SubWay Surf app on
05/15/2016. By 05/20/2016, he was on level 24.
Initially, he was very happy with the app. However, he
soon became very disappointed with the app because it
was crashing very often. As soon as he reached level 24,
he uninstalled the app."
}

Expected output:
We want to extract 3 kinds of information from each paragraph of input:
- duration if there are at least two dates,
- gender of the subject
- sentiment expressed in the text.
Once these are extracted, the data should be written out to a file.
Format : JSON

timeDuration:
Integer – time duration in days (inclusive of the given dates).

If unknown or uncomputable, then 0.

gender:
String – “male”, “female”, or “unknown”

sentiment:
String – “positive”, “negative”, “mixed” or “unknown”

Expected output for example:
{ timeDuration: 8, gender: "male", sentiment: "mixed" }

Simplifying assumptions:
For Time Duration, look only for dates of the format MM/DD/YYYY.
For Gender, look for pronouns “he” and/or “she”.
For Sentiment, look for the following keywords:
    ● Positive sentiments: Happy, Glad, Jubilant, Satisfied
    ● Negative sentiments: Sad, Disappointed, Angry, Frustrated
