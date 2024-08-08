document.addEventListener('DOMContentLoaded', function () {
    const importanceElements = document.querySelectorAll('.importance');
    const statusElements = document.querySelectorAll('.status');

    importanceElements.forEach(element => {
        const importance = element.textContent;
        let font = '';
        let textColor = '';
        let transformText = '';

        if (importance === '(상)') {
            textColor = 'text-red-500';
            transformText = "높음"
        } else if (importance === '(중)') {
            textColor = 'text-yellow-500';
            transformText = "중간"
        } else if (importance === '(하)') {
            textColor = 'text-grey-500';
            transformText = "낮음"
            
        }

        element.parentElement.classList.add(transformText, textColor);
        element.textContent = transformText;

    });

    statusElements.forEach(element => {
        const status = element.textContent;
        let bgColor = '';
        let textColor = '';
        let transformText = '';

        if (status === '[취약]') {
            bgColor = 'bg-red-200';
            textColor = 'text-white';
            transformText = 'Warning';
        } else if (status === '[인터뷰]') {
            bgColor = 'bg-yellow-200';
            textColor = 'text-white';
            transformText = 'Interview';
        } else {
            bgColor = 'bg-green-200';
            textColor = 'text-white';
            statusText = 'Good';
        }

        element.parentElement.classList.add(bgColor, textColor);
        element.textContent = transformText;
    });
});

