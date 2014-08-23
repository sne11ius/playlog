package models.daos;

import models.YoSubscriber
import models.YoSubscriber

trait YoSubscriberDAO {
  def add(subscriber: YoSubscriber);
  def findAll() : List[YoSubscriber];
}
