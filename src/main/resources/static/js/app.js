    // Função para exibir vídeos na página
    async function loadVideos() {
        try {
            const response = await fetch('/api/s3/videos');
            const videoUrls = await response.json();
            console.log(videoUrls);

            const videoGallery = $('#videoGallery');
            videoGallery.empty(); // Limpa a galeria antes de adicionar novos vídeos

            videoUrls.forEach(url => {
                const videoCard = `
                    <div class="col-md-4 mb-4">
                        <div class="card">
                            <video class="card-img-top" controls>
                                <source src="${url}" type="video/mp4">
                                Seu navegador não suporta a tag de vídeo.
                            </video>
                            <div class="card-body">
                                <p class="card-text">Clique para assistir</p>
                            </div>
                        </div>
                    </div>
                `;
                videoGallery.append(videoCard);
            });
        } catch (error) {
            console.error('Erro ao carregar os vídeos:', error);
        }
    }

    // Chama a função para carregar vídeos quando a página é carregada
    $(document).ready(function () {
        loadVideos();
    });

    // Upload de Arquivo
    $("#uploadForm").on("submit", function (e) {
        e.preventDefault();
        let formData = new FormData();
        formData.append("file", $("#uploadFile")[0].files[0]);
        formData.append("key", $("#uploadKey").val());

        $.ajax({
            url: "/api/s3/upload",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                $("#uploadResponse").html('<div class="alert alert-success">Arquivo enviado com sucesso: ' + response + '</div>');
                loadVideos(); // Recarrega os vídeos após o upload
            },
            error: function(xhr, status, error) {
                $("#uploadResponse").html('<div class="alert alert-danger">Falha no upload: ' + error + '</div>');
            }
        });
    });

    // Download de Arquivo
    $("#downloadForm").on("submit", function (e) {
        e.preventDefault();
        let key = $("#downloadKey").val();
        let downloadPath = $("#downloadPath").val();

        $.ajax({
            url: "/api/s3/download?key=" + key + "&downloadPath=" + downloadPath,
            type: "GET",
            success: function(response) {
                $("#downloadResponse").html('<div class="alert alert-success">Arquivo baixado com sucesso: ' + response + '</div>');
            },
            error: function(xhr, status, error) {
                $("#downloadResponse").html('<div class="alert alert-danger">Falha no download: ' + error + '</div>');
            }
        });
    });

    // Deletar Arquivo
    $("#deleteForm").on("submit", function (e) {
        e.preventDefault();
        let key = $("#deleteKey").val();

        $.ajax({
            url: "/api/s3/delete?key=" + key,
            type: "DELETE",
            success: function(response) {
                $("#deleteResponse").html('<div class="alert alert-success">Arquivo deletado com sucesso: ' + response + '</div>');
                loadVideos(); // Recarrega os vídeos após a deleção
            },
            error: function(xhr, status, error) {
                $("#deleteResponse").html('<div class="alert alert-danger">Falha na deleção: ' + error + '</div>');
            }
        });
    });