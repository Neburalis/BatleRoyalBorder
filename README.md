# BatleRoyalBorder

**Description:**

BatleRoyalBorder is a Minecraft mod designed for players who enjoy a more dynamic and challenging gameplay experience. This mod introduces commands that allow you to shrink the world border incrementally, creating an intense and competitive environment. Perfect for UHC (Ultra Hardcore) games or any scenario where you want to force player encounters and add a thrilling twist to your survival gameplay.

**Features:**
- **World Border Shrinking**: Use commands to gradually shrink the world border
- **Customizable Parameters**: Define the amount by which the border shrinks, the time it takes to shrink, and the delay between each shrinkage
- **Boss Bar Progress**: Visual countdown timer and shrinking progress displayed in boss bar
- **Automatic Control**: The shrinking process continues automatically according to the parameters set, freeing you to focus on the game
- **Flexible Input**: Support for time formats (s/m/h) and number shortcuts (k/m/b/t/p)

**Commands:**
All commands support the alias `/sr` for quick access.
```bash
# Configure border shrinking parameters
/simpleroyal set <shrink_amount> <shrink_time> <delay_time>
# Begin/stop the border shrinking process
/simpleroyal start
/simpleroyal stop
# Display current settings and status
/simpleroyal get
# Reset parameters to default config values
/simpleroyal reset
```

**Parameters:**
- `shrink_amount`: The amount by which the border will shrink each time (supports shortcuts like 1k, 500)
- `shrink_time`: The time it takes for the border to shrink (supports formats like 30s, 1m, .5m)
- `delay_time`: The delay between each shrink (supports same time formats)

**How to Use:**
1. Install the mod and start your Minecraft server
2. Use `/sr set` with desired parameters to configure the border shrinking
3. Use `/sr start` to initiate the border shrinking process
4. Watch as the world border gradually shrinks with boss bar countdown, increasing tension and pushing players closer together
5. Players receive boss bar updates and chat notifications throughout the process

**Example Usage:**
```
/sr set 50 30s 10s
```
This command will configure the border to shrink by 50 blocks within 30 seconds, with a 10-second delay between each shrink.

```
/sr set 1k 1m .5m  
```
This will shrink by 1000 blocks over 1 minute, with 30-second delays between shrinks. 