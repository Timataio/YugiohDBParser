# YugiohDBParser
Tool which contains many useful classes for collecting *Yu-Gi-Oh* card data, including their images, from Yugipedia. Used in IntelliJ and designed so I could create a more up-to-date LackeyCCG plugin for *Yu-gi-oh* (which I decided not to do). The class descriptions are as follows:
* **YugiohDBParser.java** takes in a txt file containing html code corresponding to card data from the official *Yu-gi-oh* database's page for a certain set. It then outputs another txt file with most of the card data. The HTML must be put in manually.
* **FileNameDownloader.java** takes in the card data from **YugiohDBParser.java** and uses it to generate a list of URLs which correspond to the card images available on Yugipedia. These URLs are outputted as .txt files in a folder.
* **ImageDownloader.java** downloads the card images using the URLs from **FileNameDownloader.java**
* **RarityParser.java** gets the rarity data from yugipedia, as it not easily obtainable using the official *Yu-gi-oh* database. Also requires manual .txt input
