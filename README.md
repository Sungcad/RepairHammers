# RepairHammers

Description
Repair hammers add configurable hammers that can fix one or more of your items. To use a hammer simply drop it on an item in you inventory that needs fixed. They can be set to fix a set amount of durability or a percentage. Hammers can be consumed on use or unlimited. If vault is installed hammers can have a cost to buy and/or use. Most messages can be changed in the config or hammers files. This plugin supports RPGItems and allows you to fix items from that plugin.

Commands
/hammer - aliases /rh /hammers
/hammer help - shows the help message from the config
/hammers list - list the hammers that are setup and how much they fix
/hammers give <player> <hammer> (number) - gives the player 1 or more hammers
/hammers buy <hammer> (number)
/hammers reload - reloads the config
/hammers info - list information about the plugin
/hammer shop - open a shop to buy hammers
/hammershop - aliases /rhshop /hammersshop - open the shop to buy hammers

Permissions
hammers.admin - gives access to the commands
hammers.command - needed to use /hammers
hammers.help - permission to show /hammers help
hammers.buy - needed to buy hammers with /hamers buy command
hammers.buy.(hammername) -permission to buy (hammername)
hammers.buyall - permission to buy all hammers
hammers.give - needed permission to give a hammer to a player
hammers.give.(hammername) - permission to give (hammername) needs hammers.give
hammers.giveall - permission to give all hammers
hammers.use - needed permission to use a hammer
hammers.use.(hammername) - permission to use (hamername) needs hammers.use
hammers.useall - permission to use all hammers
hamers.list - permission to list hammers you can give
hammers.listall - permission to see all hammers in list
hammers.shop - permission to buy hammers from /hammershop

Installation
Repair Hammers requires Java 8 to run. To install it drop the jar in your servers plugins folder then restart, edit the config as needed, and then reload it with /hammers reload. If you plan on using RPGItems hammers can only repair up to durabilityUpperBound or maxDurability whichever is lowest so make sure you set them both.