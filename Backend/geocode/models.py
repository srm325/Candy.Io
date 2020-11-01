from django.db import models
from django.core.validators import MaxValueValidator


class GPS(models.Model):
    uuid = models.IntegerField(blank=False, null=False)
    date = models.DateField(auto_now_add=True)
    address = models.CharField(max_length=50, blank=False, null=False)
    city = models.CharField(max_length=25, blank=False, null=False)
    state = models.CharField(max_length=2, blank=False, null=False)
    zip_code = models.CharField(max_length=20, blank=False, null=False)


class Symptoms(models.Model):
    uuid = models.IntegerField(blank=False, null=False)
    date = models.DateField(auto_now_add=True)
    score = models.IntegerField(validators=[MaxValueValidator(19)])
    close_contact = models.BooleanField(blank=False, null=False)

class Users(models.Model):
    uuid = models.IntegerField(unique=True, blank=False, null=False)