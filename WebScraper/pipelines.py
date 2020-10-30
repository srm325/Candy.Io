# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html


# useful for handling different item types with a single interface
import sys
import logging
import json
from itemadapter import ItemAdapter
import psycopg2

logger = logging.getLogger("Pipeline Logger")

conn = psycopg2.connect(
    database='candy',
    user='dgil',
    password='toor',
    sslmode='require',
    sslrootcert='../certs/ca.crt',
    sslkey='../certs/client.maxroach.key',
    sslcert='../certs/client.maxroach.crt',
    port=26257,
    host='localhost'
)

class CandyscrapPipeline:
    def open_spider(self, spider):
        self.file = open('items.jl', 'w')
    def close_spider(self, spider):
        self.file.close()
    def process_item(self, item, spider):
        adapter = ItemAdapter(item)
        line = json.dumps(adapter.asdict()) + "\n"
        with conn.cursor() as cur:
            if adapter.get('score'):
                try:
                    cur.execute("INSERT INTO candy.varieties (name, score) VALUES (%s, %s)", (adapter.get('name'), adapter.get('score')))
                except:
                    logger.info(sys.exc_info()[0])
                    logger.info(sys.exc_info()[1])
            else:
                try:
                    cur.execute("""INSERT INTO candy.varieties (name, score) VALUES (%s, %s)""", (adapter.get('name'), 0))
                except:
                    logger.info(sys.exc_info()[0])
                    logger.info(sys.exc_info()[1])
        conn.commit()
        return item
