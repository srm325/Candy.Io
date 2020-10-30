import logging
from bs4 import BeautifulSoup
import scrapy
from scrapy.item import Item, Field


class Candy(Item):
    name = Field()
    score = Field()

logger = logging.getLogger('myCustomLogger')

class CandydbSpider(scrapy.Spider):
    name = 'candyDB'
    allowed_domains = ['thecandydatabase.com']
    start_urls = ['https://thecandydatabase.com/category/chocolate/','https://thecandydatabase.com/category/gummi-candy/', 'https://thecandydatabase.com/category/sour/', 'https://thecandydatabase.com/category/novelty-candy/', 'https://thecandydatabase.com/category/novelty-candy/', 'https://thecandydatabase.com/category/hard-candy/']

    def parse(self, response):
        for candy in response.xpath('//a[contains(@href, "https://thecandydatabase.com/candy-item")]/@title').getall():
            c = candy.replace("'", " ")
            c = c.replace("(", "")
            c = c.replace(")", "")
            c = c.replace("/","")
            c = c.replace("\\","")
            yield Candy(name=c)
        href = response.xpath('//a[contains(@href,"page")]/@href').get()
        logger.info(href)
        return scrapy.Request(href,self.parse)
