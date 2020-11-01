from django.http import JsonResponse, HttpResponse
from django.utils.decorators import method_decorator
from django.views.generic import View
from django.views.decorators.csrf import csrf_exempt
from django.db import Error, IntegrityError
from django.db.transaction import atomic

import json
import sys
import time

from .models import *

# Warning: Do not use retry_on_exception in an inner nested transaction.
def retry_on_exception(num_retries=3, on_failure=HttpResponse(status=500), delay_=0.5, backoff_=1.5):
    def retry(view):
        def wrapper(*args, **kwargs):
            delay = delay_
            for i in range(num_retries):
                try:
                    return view(*args, **kwargs)
                except IntegrityError as ex:
                    if i == num_retries - 1:
                        return on_failure
                    elif getattr(ex.__cause__, 'pgcode', '') == errorcodes.SERIALIZATION_FAILURE:
                        time.sleep(delay)
                        delay *= backoff_
                except Error as ex:
                    return on_failure
        return wrapper
    return retry

class PingView(View):
    def get(self, request, *args, **kwargs):
        return HttpResponse("python/django", status=200)

@method_decorator(csrf_exempt, name='dispatch')
class UsersView(View):
    def get(self, request, uuid=None, *args, **kwargs):
        if uuid is None:
            users = list(Users.objects.values())
        else:
            users = list(Users.objects.filter(uuid=uuid).values())
        return JsonResponse(users, safe=False)

    @retry_on_exception(3)
    @atomic
    def post(self, request, *args, **kwargs):
        form_data = json.loads(request.body.decode())
        uuid = form_data['uuid']
        u = Users(uuid=uuid)
        u.save()
        return HttpResponse(status=200)

    @retry_on_exception(3)
    @atomic
    def delete(self, request, uuid=None, *args, **kwargs):
        if uuid is None:
            return HttpResponse(status=404)
        Users.objects.filter(uuid=uuid).delete()
        return HttpResponse(status=200)

    # The PUT method is shadowed by the POST method, so there doesn't seem
    # to be a reason to include it.

@method_decorator(csrf_exempt, name='dispatch')
class GPSView(View):
    def get(self, request, uuid=None, *args, **kwargs):
        if uuid is None:
            gps = list(GPS.objects.values())
        else:
            gps = list(GPS.objects.filter(uuid=uuid).values())
        return JsonResponse(gps, safe=False)

    @retry_on_exception(3)
    @atomic
    def post(self, request, *args, **kwargs):
        form_data = json.loads(request.body.decode())
        uuid, address, city, state, zip_code = form_data['uuid'], form_data['address'], form_data['city'], form_data['state'], form_data['zip_code']
        g = GPS(uuid=uuid, address=address, city=city, state=state, zip_code=zip_code)
        g.save()
        return HttpResponse(status=200)

    # The REST API outlined in the github does not say that /product/ needs
    # a PUT and DELETE method

@method_decorator(csrf_exempt, name='dispatch')
class SymptomsView(View):
    def get(self, request, uuid=None, *args, **kwargs):
        if uuid is None:
            symptoms = list(Symptoms.objects.values())
        else:
            symptoms = list(Symptoms.objects.filter(uuid=uuid).values())
        return JsonResponse(symptoms, safe=False)

    @retry_on_exception(3)
    @atomic
    def post(self, request, *args, **kwargs):
        form_data = json.loads(request.body.decode())
        uuid, score, close_contact = form_data['uuid'], form_data['score'], form_data['close_contact']
        s = Symptoms(uuid=uuid, score=score, close_contact=close_contact)
        s.save()
        return HttpResponse(status=200)