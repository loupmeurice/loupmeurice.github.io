<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<link
	href="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css"
	rel="stylesheet" id="bootstrap-css">
<link href="./style.css" rel="stylesheet" id="style-css">
<script
	src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
<script
	src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.1/css/bootstrap-select.css" />
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script
	src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.bundle.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.1/js/bootstrap-select.min.js"></script>
<!------ Include the above in your HEAD tag ---------->

<div class="container">
	<h1 class="text-center m-5">Suivez votre équipe préférée!</h1>

</div>


<section class="bg-dark text-center p-5 mt-4">
	<div class="container p-3">
		<h3 class="text-white">Séléctionnez l'équipe que vous voulez
			suivre et indiquez votre adresse email.</h3>
		<h4 class="text-white">Vous recevrez alors le calendrier de
			l'équipe ansi que des nouvelles automatiques (résultats des matches,
			modifications du calendrier, ...)</h4>
		<form action="#" method="Post" style="margin-top: 100px;">
			<div style="margin-bottom: 50px;">
			<h4 class="text-white">1. Sélectionnez votre équipe</h4>
				<select id="provinceSelect" class="selectpicker"
					data-live-search="true">
					<option disabled selected>Choisissez la province</option>
					<c:forEach items="${season.provinces}" var="prov" varStatus="index">
						<option value="${prov.codeName}">${prov.province}</option>
					</c:forEach>

				</select> <select class="selectpicker teamSelect" data-live-search="true">
					<option disabled selected>Choisissez l'équipe</option>
					<c:forEach items="${season.teams}" var="team" varStatus="index">
						<option class="team ${team.rank.serie.province.codeName}"
							value="${team.id}">${team.name}</option>
					</c:forEach>



				</select>
			</div>
			<div>
			<h4 class="text-white">2.Entrez votre adresse email</h4>
			<input type="text" name="text"
				placeholder="Votre adresse email">

			<button type="button" class="btn btn-default">
				S'inscrire<i class="fa fa-envelope"></i>
			</button>
			</div>
		</form>
	</div>
</section>


<footer>
	<p>© W3hubs 2018</p>
</footer>

<script>
$(document).ready(() => {
	$( "#provinceSelect" ).change(function() {
		var province = $(this).val();
		 $('.teamSelect').find('.team').hide();
		 $('.teamSelect').find('.' + province).show();
		    $('.teamSelect').selectpicker('refresh');
		
        alert( "value:" + $(this).val() );
    });
});
</script>