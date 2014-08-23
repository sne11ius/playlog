package service;

import models.YoSubscriber

trait YoService {
  
  def addSubscriber(subscriber: YoSubscriber);
  def findAll() : List[YoSubscriber];
  
  def sendYoToSubscribers(permalink: String);
  
}
