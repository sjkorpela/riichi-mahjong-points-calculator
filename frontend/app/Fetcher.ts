
interface FetchYakuProps {
    hand: object,
    winningTile: string,
    roundWind: string,
    seatWind: string,
    dora: string[],
    openHand: boolean,
    tsumo: boolean,
    flags: {
        riichi?: boolean,
        ippatsu?: boolean,
        doubleRiichi?: boolean,
        lastTile?: boolean,
        afterKan?: boolean,
        blessedHand?: boolean,
    }
}

export interface ResponseYaku {
    englishName: string,
    japaneseName: string,
    description: string,
    han: number,
    tiles?: string[],
}

export interface YakuResponse {
    status: number,
    yaku: ResponseYaku[],
    openHand: boolean,
    han: number,
    fu: number,
    score: number,
    dealer: boolean
}

export class Fetcher {

    public static async getYaku(request: FetchYakuProps): Promise<YakuResponse> {
        console.log("Fetching with: ", request)
        try {
            const raw = await fetch('http://localhost:8080/points', {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(request)
            });
            const response: YakuResponse = await raw.json();
            response.status = raw.status;
            console.log("Response: ", response);
            return response;
        } catch (error) {
            console.log("Error: ", error);
            if (error instanceof SyntaxError) {
                return {
                    dealer: false, fu: 0, han: 0, score: 0,
                    status: 400,
                    yaku: [],
                    openHand: false
                }
            } else if (error instanceof TypeError) {
                return {
                    dealer: false, fu: 0, han: 0, score: 0,
                    status: 500,
                    yaku: [],
                    openHand: false,
                }
            }
        }
        return Promise.reject();

    }
}