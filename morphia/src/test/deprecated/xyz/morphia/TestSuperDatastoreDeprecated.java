/*
  Copyright (C) 2010 Olafur Gauti Gudmundsson
  <p/>
  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
  obtain a copy of the License at
  <p/>
  http://www.apache.org/licenses/LICENSE-2.0
  <p/>
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
  and limitations under the License.
 */

package xyz.morphia;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.junit.Test;
import xyz.morphia.query.FindOptions;
import xyz.morphia.testmodel.Circle;
import xyz.morphia.testmodel.Rectangle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestSuperDatastoreDeprecated extends TestBase {
    @Test
    public void testDeleteDoesNotDeleteAnythingWhenGivenAnIncorrectId() {
        // given
        final String ns = "someCollectionName";
        getDb().getCollection(ns).remove(new BasicDBObject());

        final Rectangle rect = new Rectangle(10, 10);
        ObjectId id = new ObjectId();
        rect.setId(id);

        getAds().save(ns, rect);
        assertEquals(1, getAds().getCount(ns));

        // when giving an ID that is not the entity ID.  Note that at the time of writing this will also log a validation warning
        getAds().delete(ns, Rectangle.class, 1);

        // then
        assertEquals(1, getAds().getCount(ns));
    }

    @Test
    public void testDeleteWillRemoveAnyDocumentWithAMatchingId() {
        // given
        final String ns = "someCollectionName";
        getDb().getCollection(ns).remove(new BasicDBObject());

        final Rectangle rect = new Rectangle(10, 10);
        ObjectId rectangleId = new ObjectId();
        rect.setId(rectangleId);
        getAds().save(ns, rect);

        final Circle circle = new Circle();
        circle.setId(new ObjectId());
        getAds().save(ns, circle);

        assertEquals(2, getAds().getCount(ns));

        // when
        getAds().delete(ns, Circle.class, rectangleId);

        // then
        assertEquals(1, getAds().getCount(ns));
    }

    @Test
    public void testDeleteWithAnEntityTypeAndId() {
        // given
        final String ns = "someCollectionName";
        getDb().getCollection(ns).remove(new BasicDBObject());

        final Rectangle rect = new Rectangle(10, 10);
        ObjectId id = new ObjectId();
        rect.setId(id);

        getAds().save(ns, rect);
        assertEquals(1, getAds().getCount(ns));

        // when
        getAds().delete(ns, Rectangle.class, id);

        // then
        assertEquals(0, getAds().getCount(ns));
    }

    @Test
    public void testFind() {
        final String ns = "hotels";
        Rectangle rect = new Rectangle(10, 10);
        ObjectId id = new ObjectId();
        rect.setId(id);

        getDb().getCollection(ns).remove(new BasicDBObject());

        getAds().save(ns, rect);
        assertEquals(1, getAds().getCount(ns));
        Rectangle rectLoaded = getAds().find(ns, Rectangle.class)
                                       .find(new FindOptions().limit(1))
                                       .next();
        assertEquals(rect.getId(), rectLoaded.getId());
        assertEquals(rect.getArea(), rectLoaded.getArea(), 0);

        rect = new Rectangle(2, 1);
        getAds().save(rect); //saved to default collection name (kind)
        assertEquals(1, getAds().getCount(rect));

        rect.setId(null);
        getAds().save(rect); //saved to default collection name (kind)
        assertEquals(2, getAds().getCount(rect));

        rect = new Rectangle(4, 3);
        getAds().save(ns, rect);
        assertEquals(2, getAds().getCount(ns));

        rectLoaded = toList(getAds().find(ns, Rectangle.class).find()).get(1);
        assertEquals(rect.getId(), rectLoaded.getId());
        assertEquals(rect.getArea(), rectLoaded.getArea(), 0);

        getAds().find(ns, Rectangle.class, "_id !=", "-1", 1, 1)
                .find(new FindOptions().limit(1))
                .next();
    }

    @Test
    public void testGet() {
        final String ns = "hotels";
        final Rectangle rect = new Rectangle(10, 10);

        getDb().getCollection(ns).remove(new BasicDBObject());

        getAds().save(ns, rect);
        assertEquals(1, getAds().getCount(ns));
        final Rectangle rectLoaded = getAds().get(ns, Rectangle.class, rect.getId());
        assertEquals(rect.getId(), rectLoaded.getId());
        assertEquals(rect.getArea(), rectLoaded.getArea(), 0);
    }
}
