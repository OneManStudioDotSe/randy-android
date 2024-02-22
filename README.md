# randy

## Randy - A playground with balls, a.k.a. a random guy you met

Randy is a random canvas that is alive, moves, makes sounds and changes. Every time he is different than how he has the last time you met him.

His characteristics is his surfaces, nodes, joints, ttl of each iteration/repetition, sounds, lifetime and his elements.

### Surface (skin)

The surface of Randy can have various shapes, colours, size, movement and and limits. This translates into:

- Dynamic skin colour
    - x in [1,2, …, 10]
- Dynamic skin shape
    - [square, rectangular, circle]
- Dynamic skin size
    - [small, medium, large]
    - % of the screen width
- Dynamic skin movement range
    - [none, small, medium, large]
    - +/- value of current positions
- Dynamic skin limits
    - [bouncy, hollow]
    - bounce or pass through

### Nodes

The nodes of Randy are his vitals points. They are born, move, interact and die. This translates into:

- Dynamic amount
    - [few, some, plenty]
- Dynamic position creation
    - [random, by user]
- Dynamic birth
    - [pop, grow, fade in]
- Dynamic colour
    - x in [1, 2, …, 10] or random HEX
- Dynamic speed
- Dynamic size
    - [S, M, L]
- Dynamic collision change
    - [none, split, age in speed, age in size]

### Joints

The joints of Randy is what connects the nodes. This translates into:

- Dynamic thickness
    - [hair, normal, thick]
- Dynamic range
    - distance after which they are formed
    - [short, average, long]
- Dynamic colour

### TTL of each repetition

The life of Randy is continuous iterations, one after the other, in an endless repeat of the “breathing” of all the nodes and each node individually. This translates into:

- Dynamic TTL change
    - [none, split, age in speed, age in size]
- Dynamic notification
    - play sound

### Sounds

Randy also makes noises. Sometimes as expected, sometimes not. This translates into:

- Dynamic type
    - [breath, fart, music note]
    - Dynamic trigger
        - [bounce on walls, split]

### Lifetime

Randy, like all living things, lives and dies. When all the things that he should are done and all the things that he should say are said, Randy dies. This translates into:

- Dynamic death trigger
    - [eternal, time amount of nodes]
- Dynamic death ritual
    - [freeze, fade out, explode]

### Elements

Each person generation gives a random first name and last name to Randy. The characteristics of each person are stored as a JSON file with the values for each parameter. This was, each person can be reborn again and again.

Skin/surface: colours, shape, size, limits type, movement range

Colour: id, name, hex code, argb code

Joint: id, thickness, colour, range

Palette: id, name, list of colours

Node: id, start XY, current XY, target XY, colour, size, shape, birth type, speed, collision change, end of ttl change, collision event, end of ttl event

- Keep counter for time elapsed
- Keep counter for amount of nodes created
- Keep counter for amount of nodes destroyed
- Keep counter for amount of splits
- Keep counter for amount of ttls
- Generate random name (pick random first name and last name)
- Save current “person” as a JSON file (each characteristic’s value is saved at the JSON)
- Load a “person” (A JSON file that contains the characteristics of a person and can be loaded and played)
  Load audio file to a list of sounds
- Play audio file from a list of sounds
- Listener for wall collision event
- Listener for split event
- Color transition of the elements
- Movenemt speed based on interpolation

