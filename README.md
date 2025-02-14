> [!IMPORTANT]  
> It's usually better to use [`LegacyComponentSerializer`](https://jd.advntr.dev/text-serializer-legacy/4.18.0/net/kyori/adventure/text/serializer/legacy/LegacyComponentSerializer.html) to deserialize `String` into `Component`, and then use `MiniMessage` to serialize the `Component` back to `String`. But if you got some unusual niche case - you're welcome here.

# MiniTranslator
A small utility to convert Minecraft's legacy formatting codes to MiniMessage tags.
List of codes and tags:
```
&0 <black>
&1 <dark_blue>
&2 <dark_green>
&3 <dark_aqua>
&4 <dark_red>
&5 <dark_purple>
&6 <gold>
&7 <gray>
&8 <dark_gray>
&9 <blue>
&a <green>
&b <aqua>
&c <red>
&d <light_purple>
&e <yellow>
&f <white>

&x&1&2&3&4&5&6 <color:#123456>
&#123456 <color:#123456>
#123456 <color:#123456>

&@#abcdef-red-a& <gradient:#abcdef:red:green>

&r <reset>
&l <b>
&n <u>
&m <st>
&o <i>
&k <obf>
```
## Get it
Add jitpack repo
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Add MiniTranslator dependency. Current version: ![latest version](https://jitpack.io/v/imDaniX/MiniTranslator.svg)
```xml
<dependency>
    <groupId>com.github.imDaniX</groupId>
    <artifactId>MiniTranslator</artifactId>
    <version>v2.3</version>
</dependency>
```
