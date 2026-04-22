import {YakuResponse} from "@/app/Fetcher";

interface YakuErrorMessageProps {
    handFull: boolean,
    winningTile: boolean,
    response: YakuResponse | null,
}

export default function YakuListErrorMessage({handFull, winningTile, response}: YakuErrorMessageProps) {

    function formatText(text: string) {
        return (
            <div className="my-box">
                <h1 className="text-xl decoration-2 text-white font-bold text-wrap">{text}</h1>
            </div>
        )
    }

    if (handFull && winningTile && response && response.status == 500) {
        return formatText("The service is currently unavailable...");
    } else if (handFull && winningTile && response && response.status == 400) {
        return formatText("Your hand doesn't seem to be valid.");
    } if (handFull && winningTile && response && response.status == 200 && response.yaku.length == 0) {
        return formatText("Your hand doesn't fulfill any Yaku.")
    } else if (handFull && winningTile && response && response.status == 200) {
        return null;
    } else if (handFull && winningTile && !response) {
        return formatText("Loading...");
    } else {
        return formatText("Complete your hand and winning tile to see its Yaku.");
    }

}