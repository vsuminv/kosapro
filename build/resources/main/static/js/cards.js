document.addEventListener('DOMContentLoaded', function() {
    // card1Chart 생성
    var ctx1 = document.getElementById('card1Chart').getContext('2d');
    var card1Chart = new Chart(ctx1, {
        type: 'pie',
        data: {
            labels: ['서버 점수', '점검 서버수', '서버 구성'],
            datasets: [{
                data: [80, 10, 20],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.8)',
                    'rgba(54, 162, 235, 0.8)',
                    'rgba(255, 206, 86, 0.8)'
                ]
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                },
                title: {
                    display: true,
                    text: '서버 상태'
                }
            }
        }
    });

    // card2Chart 생성 (막대 그래프)
    var ctx2 = document.getElementById('card2Chart').getContext('2d');
    var card2Chart = new Chart(ctx2, {
        type: 'bar',
        data: {
            labels: ['양호', '취약', '인터뷰 필요', 'N/A'],
            datasets: [{
                label: '진단 상태',
                data: [10, 5, 3, 2],
                backgroundColor: [
                    'rgba(75, 192, 192, 0.8)',
                    'rgba(255, 99, 132, 0.8)',
                    'rgba(255, 206, 86, 0.8)',
                    'rgba(54, 162, 235, 0.8)'
                ]
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    precision: 0
                }
            }
        }
    });

    // card3Chart 생성 (레이더 차트)
    var ctx3 = document.getElementById('card3Chart').getContext('2d');
    var card3Chart = new Chart(ctx3, {
        type: 'radar',
        data: {
            labels: ['계정 관리', '로그 관리', '파일 및 디렉토리 관리'],
            datasets: [{
                label: '영역별 점수',
                data: [80, 90, 70],
                fill: true,
                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                borderColor: 'rgb(255, 99, 132)',
                pointBackgroundColor: 'rgb(255, 99, 132)',
                pointBorderColor: '#fff',
                pointHoverBackgroundColor: '#fff',
                pointHoverBorderColor: 'rgb(255, 99, 132)'
            }]
        },
        options: {
            responsive: true,
            scales: {
                r: {
                    angleLines: {
                        display: false
                    },
                    suggestedMin: 0,
                    suggestedMax: 100
                }
            }
        }
    });
});