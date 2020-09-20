# Quotifier
A simple Discord bot which converts text quotes to fancy images

# Usage
Add to your discord server [here](https://discord.com/oauth2/authorize?client_id=757290872520835192&permissions=109568&scope=bot)

Invoke using the command ```~quotify [user] [message]```:

```~quotify @wumpus``` converts the latest message sent by the user ```wumpus``` to a quote image

```~quotify wumpus#0001 I have no friends``` produces a quote image and attributes it to ```wumpus```

Additional options can be specified using the command ```~quotify{options} [user] [message]```:

```~quotify{img:[url]} wumpus#1234``` produces a quote image with the background image specified by the url

```~quotify{font:[font],img:[url]} wumpus#1234``` produces a quote image with a custom font and the background image specified by the url
