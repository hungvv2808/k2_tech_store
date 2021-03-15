$(document).ready(function() {
    setTimeout(loginModal, 3000);
});
function loginModal() {
    $('#login-modal').modal('show');
    $(function () {
        $('[data-toggle="tooltip"]').tooltip()
    })
}