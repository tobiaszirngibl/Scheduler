from django.contrib.auth.decorators import login_required
from django.shortcuts import redirect, render


# Create your views here.

@login_required
def profile_view(request):
	user = {"email": request.user.email,
			"firstname": request.user.first_name,
			"lastname": request.user.last_name,
			"contact_notes": request.user.get_contact_notes,
			"education": request.user.get_education,
			"phone": request.user.phone,
			"skills": request.user.skills,
			"spare_time": request.user.spare_time,
			"location": request.user.location,
			"job": request.user.job
			}
	return render(request, 'website/profile.html', {"user": user})

@login_required
def calendar_view(request):
	user = {"email": request.user.email,
			"firstname": request.user.first_name,
			"lastname": request.user.last_name}
	return render(request, 'website/calendar.html', {"user": user})

@login_required
def group_view(request):
	user = {"email": request.user.email,
			"firstname": request.user.first_name,
			"lastname": request.user.last_name}
	return render(request, 'website/groups.html', {"user": user})



@login_required
def login_redirect(request):
    return HttpResponseRedirect('/accounts/private_profile/%d/'%request.user.id)